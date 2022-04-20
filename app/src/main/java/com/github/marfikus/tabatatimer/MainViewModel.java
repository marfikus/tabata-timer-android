package com.github.marfikus.tabatatimer;

import android.os.CountDownTimer;
import android.text.Editable;

import androidx.lifecycle.ViewModel;


public class MainViewModel extends ViewModel {
    private MainActivityCallback mainActivityCallback = null;

    private final AppSettings appSettings;
    private final SoundPlayer soundPlayer;
    private final MyWakeLock myWakeLock;

    private boolean timersChainStarted = false;
    private int workTime;
    private int restTime;
    private int loopCount;
    private int startDelayTime;
    private CountDownTimer startDelayTimer;
    private CountDownTimer workTimer;
    private CountDownTimer restTimer;


    public MainViewModel(AppSettings settings, SoundPlayer player, MyWakeLock wakeLock) {
        appSettings = settings;
        soundPlayer = player;
        myWakeLock = wakeLock;
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
                if (callbackAttached()) mainActivityCallback.updateCurrentTimeView(visibleStartDelayTime);
            }

            @Override
            public void onFinish() {
                if (callbackAttached()) {
                    mainActivityCallback.updateCurrentStateView(R.string.current_state_work);
                    mainActivityCallback.updateCurrentTimeView(workTime);
                    mainActivityCallback.updateCurrentLoopView(currentLoop.getValue());
                }
                soundPlayer.playDing();
                workTimer.start();
            }
        };

        workTimer = new CountDownTimer(workTime * 1000, 1000) {
            int visibleWorkTime = workTime + 1;

            @Override
            public void onTick(long l) {
                visibleWorkTime -= 1;
                if (callbackAttached()) mainActivityCallback.updateCurrentTimeView(visibleWorkTime);
            }

            @Override
            public void onFinish() {
                if (currentLoop.getValue() < loopCount) {
                    currentLoop.incValue();
                    visibleWorkTime = workTime + 1;
                    if (callbackAttached()) {
                        mainActivityCallback.updateCurrentStateView(R.string.current_state_rest);
                        mainActivityCallback.updateCurrentTimeView(restTime);
                    }
                    soundPlayer.playDing();
                    restTimer.start();

                } else {
                    soundPlayer.playTada();
                    stopTimersChain();
                    if (callbackAttached()) mainActivityCallback.unlockInputFields();
                }
            }
        };

        restTimer = new CountDownTimer(restTime * 1000, 1000) {
            int visibleRestTime = restTime + 1;

            @Override
            public void onTick(long l) {
                visibleRestTime -= 1;
                if (callbackAttached()) mainActivityCallback.updateCurrentTimeView(visibleRestTime);
            }

            @Override
            public void onFinish() {
                visibleRestTime = restTime + 1;
                if (callbackAttached()) {
                    mainActivityCallback.updateCurrentStateView(R.string.current_state_work);
                    mainActivityCallback.updateCurrentTimeView(workTime);
                    mainActivityCallback.updateCurrentLoopView(currentLoop.getValue());
                }
                soundPlayer.playDing();
                workTimer.start();
            }
        };

        // общее время работы + еще минута сверху (на всякий случай)
        long totalTime = (workTime + restTime) * loopCount + startDelayTime + 60;
        myWakeLock.start(totalTime);

        if (startDelayTime > 0) {
            if (callbackAttached()) {
                mainActivityCallback.updateCurrentStateView(R.string.current_state_start_delay);
                mainActivityCallback.updateCurrentTimeView(startDelayTime);
            }
            startDelayTimer.start();
        } else {
            if (callbackAttached()) {
                mainActivityCallback.updateCurrentStateView(R.string.current_state_work);
                mainActivityCallback.updateCurrentLoopView(currentLoop.getValue());
                mainActivityCallback.updateCurrentTimeView(workTime);
            }
            soundPlayer.playDing();
            workTimer.start();
        }

        if (callbackAttached()) mainActivityCallback.updateStartButtonCaption(R.string.start_button_stop);
        timersChainStarted = true;
    }

    private void stopTimersChain() {
        if (startDelayTimer != null) startDelayTimer.cancel();
        if (workTimer != null) workTimer.cancel();
        if (restTimer != null) restTimer.cancel();
        timersChainStarted = false;
        myWakeLock.stop();

        if (callbackAttached()) {
            mainActivityCallback.updateStartButtonCaption(R.string.start_button_start);
            mainActivityCallback.updateCurrentStateView(R.string.current_state_stopped);
            mainActivityCallback.updateCurrentTimeView(0);
            mainActivityCallback.updateCurrentLoopView(0);
        }
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
}
