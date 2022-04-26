package com.github.marfikus.tabatatimer;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
        mainViewModel.loadSettings();

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(view -> mainViewModel.startButtonClicked(
                workTimeInput.getText(),
                restTimeInput.getText(),
                loopCountInput.getText(),
                startDelayTimeInput.getText()
        ));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mainViewModel.callbackAttached()) mainViewModel.attachCallback(this);
        mainViewModel.updateViews();
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

    @Override
    public void showWorkTimeInputError(@StringRes int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
        workTimeInput.requestFocus();
    }

    @Override
    public void showRestTimeInputError(@StringRes int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
        restTimeInput.requestFocus();
    }

    @Override
    public void showLoopCountInputError(@StringRes int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
        loopCountInput.requestFocus();
    }

    @Override
    public void showStartDelayTimeInputError(@StringRes int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
        startDelayTimeInput.requestFocus();
    }

    @Override
    public void updateCurrentTimeView(int value) {
        currentTimeView.setText(String.valueOf(value));
    }

    @Override
    public void updateCurrentStateView(@StringRes int value) {
        currentStateView.setText(getString(value));
    }

    @Override
    public void updateCurrentLoopView(int value) {
        currentLoopView.setText(String.valueOf(value));
    }

    @Override
    public void updateStartButtonCaption(@StringRes int value) {
        startButton.setText(getString(value));
    }
}