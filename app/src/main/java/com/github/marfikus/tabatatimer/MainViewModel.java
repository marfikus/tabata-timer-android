package com.github.marfikus.tabatatimer;

import android.os.CountDownTimer;
import android.text.Editable;

import androidx.lifecycle.ViewModel;


public class MainViewModel extends ViewModel {
    private MainActivityCallback mainActivityCallback = null;

    private final AppSettings appSettings;
    private final SoundPlayer soundPlayer;
    private final MyWakeLock myWakeLock;
    private final CurrentValues currentValues;

    private boolean timersChainStarted = false;
    private int workTime;
    private int restTime;
    private int loopCount;
    private int startDelayTime;
    private CountDownTimer startDelayTimer;
    private CountDownTimer workTimer;
    private CountDownTimer restTimer;


    public MainViewModel(AppSettings appSettings, SoundPlayer soundPlayer, MyWakeLock myWakeLock, CurrentValues currentValues) {
        this.appSettings = appSettings;
        this.soundPlayer = soundPlayer;
        this.myWakeLock = myWakeLock;
        this.currentValues = currentValues;
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
        } else {
            if (checkInputFieldsValues(
                    workTimeValue,
                    restTimeValue,
                    loopCountValue,
                    startDelayTimeValue
            )) {
                saveSettings();
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

        startDelayTimer = new CountDownTimer(startDelayTime * 1000, 1000) {
            int visibleStartDelayTime = startDelayTime + 1;

            @Override
            public void onTick(long l) {
                visibleStartDelayTime -= 1;
                currentValues.setTime(visibleStartDelayTime);
                if (callbackAttached()) mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
            }

            @Override
            public void onFinish() {
                currentValues.setState(R.string.current_state_work);
                currentValues.setLoop(1);
                currentValues.setTime(workTime);
                if (callbackAttached()) {
                    mainActivityCallback.updateCurrentStateView(currentValues.getState());
                    mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
                    mainActivityCallback.updateCurrentLoopView(currentValues.getLoop());
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
                currentValues.setTime(visibleWorkTime);
                if (callbackAttached()) mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
            }

            @Override
            public void onFinish() {
                if (currentValues.getLoop() < loopCount) {
                    visibleWorkTime = workTime + 1;
                    currentValues.setState(R.string.current_state_rest);
                    currentValues.setTime(restTime);
                    if (callbackAttached()) {
                        mainActivityCallback.updateCurrentStateView(currentValues.getState());
                        mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
                    }
                    soundPlayer.playDing();
                    restTimer.start();

                } else {
                    soundPlayer.playTada();
                    stopTimersChain();
                    currentValues.setInputsEnabled(true);
                    if (callbackAttached()) mainActivityCallback.unlockInputFields();
                }
            }
        };

        restTimer = new CountDownTimer(restTime * 1000, 1000) {
            int visibleRestTime = restTime + 1;

            @Override
            public void onTick(long l) {
                visibleRestTime -= 1;
                currentValues.setTime(visibleRestTime);
                if (callbackAttached()) mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
            }

            @Override
            public void onFinish() {
                visibleRestTime = restTime + 1;
                currentValues.incLoop();
                currentValues.setState(R.string.current_state_work);
                currentValues.setTime(workTime);
                if (callbackAttached()) {
                    mainActivityCallback.updateCurrentStateView(currentValues.getState());
                    mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
                    mainActivityCallback.updateCurrentLoopView(currentValues.getLoop());
                }
                soundPlayer.playDing();
                workTimer.start();
            }
        };


        // общее время работы + еще минута сверху (на всякий случай)
        long totalTime = (workTime + restTime) * loopCount + startDelayTime + 60;
        myWakeLock.start(totalTime);
        // предотвращаем погасание экрана на время работы таймера
        // (для старых смартов, ибо там myWakeLock не работает)
        currentValues.setKeepScreenOn(true);

        if (startDelayTime > 0) {
            currentValues.setState(R.string.current_state_start_delay);
            currentValues.setTime(startDelayTime);
            startDelayTimer.start();
        } else {
            currentValues.setState(R.string.current_state_work);
            currentValues.setLoop(1);
            currentValues.setTime(workTime);
            soundPlayer.playDing();
            workTimer.start();
        }

        currentValues.setStartButtonCaption(R.string.start_button_stop);
        currentValues.setInputsEnabled(false);
        timersChainStarted = true;
        updateViews();
    }

    private void stopTimersChain() {
        if (startDelayTimer != null) startDelayTimer.cancel();
        if (workTimer != null) workTimer.cancel();
        if (restTimer != null) restTimer.cancel();
        timersChainStarted = false;

        currentValues.setStartButtonCaption(R.string.start_button_start);
        currentValues.setState(R.string.current_state_stopped);
        currentValues.setTime(0);
        currentValues.setLoop(0);
        currentValues.setInputsEnabled(true);
        currentValues.setKeepScreenOn(false);
        updateViews();

        myWakeLock.stop();
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

    public void updateViews() {
        if (callbackAttached()) {
            mainActivityCallback.updateStartButtonCaption(currentValues.getStartButtonCaption());
            mainActivityCallback.updateCurrentStateView(currentValues.getState());
            mainActivityCallback.updateCurrentTimeView(currentValues.getTime());
            mainActivityCallback.updateCurrentLoopView(currentValues.getLoop());

            if (currentValues.isInputsEnabled()) {
                mainActivityCallback.unlockInputFields();
            } else {
                mainActivityCallback.lockInputFields();
            }

            mainActivityCallback.keepScreenOn(currentValues.isKeepScreenOn());
        }
    }
}
