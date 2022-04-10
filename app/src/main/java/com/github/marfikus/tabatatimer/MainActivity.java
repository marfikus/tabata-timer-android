package com.github.marfikus.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool;
    private AssetManager assetManager;
    private int soundDing, soundTada;

    private EditText workTimeInput;
    private EditText restTimeInput;
    private EditText loopCountInput;
    private EditText startDelayTimeInput;
    private Button startButton;
    private TextView currentStateView;
    private TextView currentLoopView;
    private TextView currentTimeView;

    private AppSettings appSettings;

    private boolean timersChainStarted = false;
    private int workTime;
    private int restTime;
    private int loopCount;
    private int startDelayTime;
    private CountDownTimer startDelayTimer;
    private CountDownTimer workTimer;
    private CountDownTimer restTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        assetManager = getAssets();
        soundDing = loadSound("ding.mp3");
        soundTada = loadSound("tada.mp3");

        workTimeInput = findViewById(R.id.work_time_input);
        restTimeInput = findViewById(R.id.rest_time_input);
        loopCountInput = findViewById(R.id.loop_count_input);
        startDelayTimeInput = findViewById(R.id.start_delay_time_input);
        currentStateView = findViewById(R.id.current_state);
        currentLoopView = findViewById(R.id.current_loop);
        currentTimeView = findViewById(R.id.current_time);

        appSettings = new AppSettings(getApplicationContext());
        loadSettings();

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(view -> {
            if (timersChainStarted) {
                stopTimersChain();
                unlockFields();
            } else {
                if (checkFields()) {
                    saveSettings();
                    lockFields();
                    startTimersChain();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        appSettings = null;
    }

    private boolean checkFields() {
        String workTimeString = workTimeInput.getText().toString().trim();
        if (workTimeString.isEmpty()) {
            Toast.makeText(this, "Work time field is empty!", Toast.LENGTH_SHORT).show();
            workTimeInput.requestFocus();
            return false;
        }
        workTime = Integer.parseInt(workTimeString);
        if (workTime <= 0) {
            Toast.makeText(this, "Work time field <= 0!", Toast.LENGTH_SHORT).show();
            workTimeInput.requestFocus();
            return false;
        }

        String restTimeString = restTimeInput.getText().toString().trim();
        if (restTimeString.isEmpty()) {
            Toast.makeText(this, "Rest time field is empty!", Toast.LENGTH_SHORT).show();
            restTimeInput.requestFocus();
            return false;
        }
        restTime = Integer.parseInt(restTimeString);
        if (restTime <= 0) {
            Toast.makeText(this, "Rest time field <= 0!", Toast.LENGTH_SHORT).show();
            restTimeInput.requestFocus();
            return false;
        }

        String loopCountString = loopCountInput.getText().toString().trim();
        if (loopCountString.isEmpty()) {
            Toast.makeText(this, "Loop count field is empty!", Toast.LENGTH_SHORT).show();
            loopCountInput.requestFocus();
            return false;
        }
        loopCount = Integer.parseInt(loopCountString);
        if (loopCount <= 0) {
            Toast.makeText(this, "Loop count field <= 0!", Toast.LENGTH_SHORT).show();
            loopCountInput.requestFocus();
            return false;
        }

        String startDelayTimeString = startDelayTimeInput.getText().toString().trim();
        if (startDelayTimeString.isEmpty()) {
            Toast.makeText(this, "Start delay time field is empty!", Toast.LENGTH_SHORT).show();
            startDelayTimeInput.requestFocus();
            return false;
        }
        startDelayTime = Integer.parseInt(startDelayTimeString);
        if (startDelayTime < 0) {
            Toast.makeText(this, "Start delay time field < 0!", Toast.LENGTH_SHORT).show();
            startDelayTimeInput.requestFocus();
            return false;
        }

        return true;
    }

    private void lockFields() {
        workTimeInput.setEnabled(false);
        restTimeInput.setEnabled(false);
        loopCountInput.setEnabled(false);
        startDelayTimeInput.setEnabled(false);
    }

    private void unlockFields() {
        workTimeInput.setEnabled(true);
        restTimeInput.setEnabled(true);
        loopCountInput.setEnabled(true);
        startDelayTimeInput.setEnabled(true);
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
        startButton.setText(R.string.start_button_start);
        currentStateView.setText(R.string.current_state_stopped);
        currentTimeView.setText(R.string.zero);
        currentLoopView.setText(R.string.zero);
    }

    private void loadSettings() {
        workTimeInput.setText(Integer.toString(appSettings.getWorkTime()));
        restTimeInput.setText(Integer.toString(appSettings.getRestTime()));
        loopCountInput.setText(Integer.toString(appSettings.getLoopCount()));
    }

    private void saveSettings() {
        appSettings.updateWorkTime(workTime);
        appSettings.updateRestTime(restTime);
        appSettings.updateLoopCount(loopCount);
    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd = null;
        try {
            afd = assetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't load file '" + fileName + "'", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return soundPool.load(afd, 1);
    }

    private void playSound(int sound) {
        if (sound > 0)
            soundPool.play(sound, 1, 1, 1, 0, 1);
    }
}