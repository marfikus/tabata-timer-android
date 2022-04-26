package com.github.marfikus.tabatatimer;

import androidx.annotation.StringRes;


public interface MainActivityCallback {

    void updateInputFields(
            String workTime,
            String restTime,
            String loopCount,
            String startDelayTime
    );

    void lockInputFields();

    void unlockInputFields();

    void showWorkTimeInputError(@StringRes int message);

    void showRestTimeInputError(@StringRes int message);

    void showLoopCountInputError(@StringRes int message);

    void showStartDelayTimeInputError(@StringRes int message);

    void updateCurrentTimeView(int value);

    void updateCurrentStateView(@StringRes int value);

    void updateCurrentLoopView(int value);

    void updateStartButtonCaption(@StringRes int value);

    void keepScreenOn(boolean value);
}
