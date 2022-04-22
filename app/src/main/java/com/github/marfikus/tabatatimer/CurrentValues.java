package com.github.marfikus.tabatatimer;

import androidx.annotation.StringRes;

public class CurrentValues {
    private boolean inputsEnabled;
    private int startButtonCaption;
    private int state;
    private int time;
    private int loop;

    public CurrentValues(
            boolean inputsEnabled,
            @StringRes int startButtonCaption,
            @StringRes int state,
            int time,
            int loop
    ) {
        this.inputsEnabled = inputsEnabled;
        this.startButtonCaption = startButtonCaption;
        this.state = state;
        this.time = time;
        this.loop = loop;
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
}
