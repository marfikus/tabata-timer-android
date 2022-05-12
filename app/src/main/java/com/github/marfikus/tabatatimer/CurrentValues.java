package com.github.marfikus.tabatatimer;

import androidx.annotation.StringRes;

public class CurrentValues {
    private boolean inputsEnabled;
    private int startButtonCaption;
    private int state;
    private int time;
    private int loop;
    private boolean keepScreenOn;

    public CurrentValues(
            boolean inputsEnabled,
            @StringRes int startButtonCaption,
            @StringRes int state,
            int time,
            int loop,
            boolean keepScreenOn
    ) {
        this.inputsEnabled = inputsEnabled;
        this.startButtonCaption = startButtonCaption;
        this.state = state;
        this.time = time;
        this.loop = loop;
        this.keepScreenOn = keepScreenOn;
    }

    public boolean isInputsEnabled() {
        return inputsEnabled;
    }

    public void setInputsEnabled(boolean inputsEnabled) {
        this.inputsEnabled = inputsEnabled;
    }

    public int getStartButtonCaption() {
        return startButtonCaption;
    }

    public void setStartButtonCaption(@StringRes int startButtonCaption) {
        this.startButtonCaption = startButtonCaption;
    }

    public int getState() {
        return state;
    }

    public void setState(@StringRes int state) {
        this.state = state;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public void incLoop() {
        this.loop += 1;
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }
}
