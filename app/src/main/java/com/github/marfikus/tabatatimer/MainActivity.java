package com.github.marfikus.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MainActivityCallback {
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

        mainViewModel.attachCallback(this);
        // TODO: 19.04.22 добавить проверку бандла: либо из него грузить значения, либо сохраненные настройки
        mainViewModel.loadSettings();

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(view -> {
            mainViewModel.startButtonClicked();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mainViewModel.callbackAttached()) mainViewModel.attachCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mainViewModel.detachCallback();
    }

    @Override
    public void updateInputFields(String workTime, String restTime, String loopCount, String startDelayTime) {
        workTimeInput.setText(workTime);
        restTimeInput.setText(restTime);
        loopCountInput.setText(loopCount);
        startDelayTimeInput.setText(startDelayTime);
    }

    @Override
    public void lockInputFields() {
        workTimeInput.setEnabled(false);
        restTimeInput.setEnabled(false);
        loopCountInput.setEnabled(false);
        startDelayTimeInput.setEnabled(false);
    }

    @Override
    public void unlockInputFields() {
        workTimeInput.setEnabled(true);
        restTimeInput.setEnabled(true);
        loopCountInput.setEnabled(true);
        startDelayTimeInput.setEnabled(true);
    }
}