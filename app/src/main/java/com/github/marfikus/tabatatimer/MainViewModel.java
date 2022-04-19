package com.github.marfikus.tabatatimer;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.text.Editable;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.IOException;

public class MainViewModel extends ViewModel {
    private MainActivityCallback mainActivityCallback = null;

    private SoundPool soundPool;
    private AssetManager assetManager;
    private int soundDing, soundTada;

    private AppSettings appSettings;

    private OneTimeWorkRequest workRequest;

    private boolean timersChainStarted = false;
    private int workTime;
    private int restTime;
    private int loopCount;
    private int startDelayTime;
    private CountDownTimer startDelayTimer;
    private CountDownTimer workTimer;
    private CountDownTimer restTimer;


    public MainViewModel(AppSettings settings) {
        appSettings = settings;
    }

    public void init() {
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        assetManager = getAssets();
        soundDing = loadSound("ding.mp3");
        soundTada = loadSound("tada.mp3");
    }

    public void attachCallback(MainActivityCallback callback) {
        mainActivityCallback = callback;
    }

    public void detachCallback() {
        mainActivityCallback = null;
    }

    public boolean callbackAttached() {
        return mainActivityCallback != null;
    }


    public void startButtonClicked(
            Editable workTimeValue,
            Editable restTimeValue,
            Editable loopCountValue,
            Editable startDelayTimeValue
    ) {
        if (timersChainStarted) {
            stopTimersChain();
            if (callbackAttached()) mainActivityCallback.unlockInputFields();
        } else {
            if (checkInputFieldsValues(
                    workTimeValue,
                    restTimeValue,
                    loopCountValue,
                    startDelayTimeValue
            )) {
                saveSettings();
                if (callbackAttached()) mainActivityCallback.lockInputFields();
                startTimersChain();
            }
        }
    }

    private boolean checkInputFieldsValues(
            Editable workTimeValue,
            Editable restTimeValue,
            Editable loopCountValue,
            Editable startDelayTimeValue
    ) {
        String workTimeString = workTimeValue.toString().trim();
        if (workTimeString.isEmpty()) {
            if (callbackAttached()) mainActivityCallback.showWorkTimeInputError(R.string.work_time_empty);
            return false;
        }
        workTime = Integer.parseInt(workTimeString);
        if (workTime <= 0) {
            if (callbackAttached()) mainActivityCallback.showWorkTimeInputError(R.string.work_time_less_0);
            return false;
        }

        String restTimeString = restTimeValue.toString().trim();
        if (restTimeString.isEmpty()) {
            if (callbackAttached()) mainActivityCallback.showRestTimeInputError(R.string.rest_time_empty);
            return false;
        }
        restTime = Integer.parseInt(restTimeString);
        if (restTime <= 0) {
            if (callbackAttached()) mainActivityCallback.showRestTimeInputError(R.string.rest_time_less_0);
            return false;
        }

        String loopCountString = loopCountValue.toString().trim();
        if (loopCountString.isEmpty()) {
            if (callbackAttached()) mainActivityCallback.showLoopCountInputError(R.string.loop_count_empty);
            return false;
        }
        loopCount = Integer.parseInt(loopCountString);
        if (loopCount <= 0) {
            if (callbackAttached()) mainActivityCallback.showLoopCountInputError(R.string.loop_count_less_0);
            return false;
        }

        String startDelayTimeString = startDelayTimeValue.toString().trim();
        if (startDelayTimeString.isEmpty()) {
            if (callbackAttached()) mainActivityCallback.showStartDelayTimeInputError(R.string.start_delay_time_empty);
            return false;
        }
        startDelayTime = Integer.parseInt(startDelayTimeString);
        if (startDelayTime < 0) {
            if (callbackAttached()) mainActivityCallback.showStartDelayTimeInputError(R.string.start_delay_time_less_0);
            return false;
        }

        return true;
    }

    private void startTimersChain() {
        // сделал класс для доступа к значению currentLoop из обоих таймеров
        // (костыль, но другого решения пока не придумал =))
        CurrentLoop currentLoop = new CurrentLoop(1);

        startDelayTimer = new CountDownTimer(startDelayTime * 1000, 1000) {
            int visibleStartDelayTime = startDelayTime + 1;

            @Override
            public void onTick(long l) {
                visibleStartDelayTime -= 1;
                currentTimeView.setText(String.valueOf(visibleStartDelayTime));
            }

            @Override
            public void onFinish() {
                currentStateView.setText(R.string.current_state_work);
                currentTimeView.setText(String.valueOf(workTime));
                currentLoopView.setText(String.valueOf(currentLoop.getValue()));
                playSound(soundDing);
                workTimer.start();
            }
        };

        workTimer = new CountDownTimer(workTime * 1000, 1000) {
            int visibleWorkTime = workTime + 1;

            @Override
            public void onTick(long l) {
                visibleWorkTime -= 1;
                currentTimeView.setText(String.valueOf(visibleWorkTime));
            }

            @Override
            public void onFinish() {
                if (currentLoop.getValue() < loopCount) {
                    currentLoop.incValue();
                    visibleWorkTime = workTime + 1;
                    currentStateView.setText(R.string.current_state_rest);
                    currentTimeView.setText(String.valueOf(restTime));
                    playSound(soundDing);
                    restTimer.start();

                } else {
                    playSound(soundTada);
                    stopTimersChain();
                    unlockFields();
                }
            }
        };

        restTimer = new CountDownTimer(restTime * 1000, 1000) {
            int visibleRestTime = restTime + 1;

            @Override
            public void onTick(long l) {
                visibleRestTime -= 1;
                currentTimeView.setText(String.valueOf(visibleRestTime));
            }

            @Override
            public void onFinish() {
                visibleRestTime = restTime + 1;
                currentStateView.setText(R.string.current_state_work);
                currentTimeView.setText(String.valueOf(workTime));
                currentLoopView.setText(String.valueOf(currentLoop.getValue()));
                playSound(soundDing);
                workTimer.start();
            }
        };

        // общее время работы + еще минута сверху (на всякий случай)
        long totalTime = (workTime + restTime) * loopCount + startDelayTime + 60;
        Data data = new Data.Builder()
                .putLong("TOTAL_TIME", totalTime)
                .build();
        workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);

        if (startDelayTime > 0) {
            currentStateView.setText(R.string.current_state_start_delay);
            currentTimeView.setText(String.valueOf(startDelayTime));
            startDelayTimer.start();
        } else {
            currentStateView.setText(R.string.current_state_work);
            currentLoopView.setText(String.valueOf(currentLoop.getValue()));
            currentTimeView.setText(String.valueOf(workTime));
            playSound(soundDing);
            workTimer.start();
        }

        startButton.setText(R.string.start_button_stop);
        timersChainStarted = true;
    }

    private void stopTimersChain() {
        if (startDelayTimer != null) startDelayTimer.cancel();
        if (workTimer != null) workTimer.cancel();
        if (restTimer != null) restTimer.cancel();
        timersChainStarted = false;

        WorkManager.getInstance(getApplicationContext()).cancelWorkById(workRequest.getId());

        startButton.setText(R.string.start_button_start);
        currentStateView.setText(R.string.current_state_stopped);
        currentTimeView.setText(R.string.zero);
        currentLoopView.setText(R.string.zero);
    }


    public void loadSettings() {
        if (callbackAttached()) mainActivityCallback.updateInputFields(
                Integer.toString(appSettings.getWorkTime()),
                Integer.toString(appSettings.getRestTime()),
                Integer.toString(appSettings.getLoopCount()),
                Integer.toString(appSettings.getStartDelayTime())
        );
    }

    private void saveSettings() {
        appSettings.updateWorkTime(workTime);
        appSettings.updateRestTime(restTime);
        appSettings.updateLoopCount(loopCount);
        appSettings.updateStartDelayTime(startDelayTime);
    }


    private int loadSound(String fileName) {
        AssetFileDescriptor afd = null;
        try {
            afd = assetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.open_file_error) + fileName + "'", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return soundPool.load(afd, 1);
    }

    private void playSound(int sound) {
        if (sound > 0)
            soundPool.play(sound, 1, 1, 1, 0, 1);
    }
}
