package com.github.marfikus.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SoundPool mSoundPool;
    private AssetManager mAssetManager;
    private int mDing, mTada;

    private EditText workTimeInput;
    private EditText restTimeInput;
    private EditText loopCountInput;
    private Button startButton;
    private TextView currentState;
    private TextView currentLoop;
    private TextView currentTime;

    AppSettings appSettings;

    private boolean timerStarted = false;
    int workTime;
    int restTime;
    int loopCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mAssetManager = getAssets();
        mDing = loadSound("ding.mp3");
        mTada = loadSound("tada.mp3");

        workTimeInput = findViewById(R.id.work_time_input);
        restTimeInput = findViewById(R.id.rest_time_input);
        loopCountInput = findViewById(R.id.loop_count_input);
        currentState = findViewById(R.id.current_state);
        currentLoop = findViewById(R.id.current_loop);
        currentTime = findViewById(R.id.current_time);

        appSettings = new AppSettings(getApplicationContext());
        loadSettings();

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(view -> {
            if (timerStarted) {
                stopTimer();
            } else {
                if (checkFields()) {
                    startTimer();
                    saveSettings();
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

        return true;
    }

    private void startTimer() {
        startButton.setText(R.string.start_button_stop);
        currentState.setText(R.string.current_state_work);



        playSound(mDing);
        timerStarted = true;
    }

    private void stopTimer() {
        startButton.setText(R.string.start_button_start);
        currentState.setText(R.string.current_state_stopped);
        timerStarted = false;
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
            afd = mAssetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't load file '" + fileName + "'", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

    private void playSound(int sound) {
        if (sound > 0)
            mSoundPool.play(sound, 1, 1, 1, 0, 1);
    }
}