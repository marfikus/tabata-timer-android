package com.github.marfikus.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
    private MainViewModel mainViewModel;

    private EditText workTimeInput;
    private EditText restTimeInput;
    private EditText loopCountInput;
    private EditText startDelayTimeInput;
    private Button startButton;
    private TextView currentStateView;
    private TextView currentLoopView;
    private TextView currentTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = ((App) getApplication()).mainViewModel;

        workTimeInput = findViewById(R.id.work_time_input);
        restTimeInput = findViewById(R.id.rest_time_input);
        loopCountInput = findViewById(R.id.loop_count_input);
        startDelayTimeInput = findViewById(R.id.start_delay_time_input);
        currentStateView = findViewById(R.id.current_state);
        currentLoopView = findViewById(R.id.current_loop);
        currentTimeView = findViewById(R.id.current_time);

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(view -> {
            mainViewModel.startButtonClicked();
        });

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
}