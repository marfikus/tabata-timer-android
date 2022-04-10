package com.github.marfikus.tabatatimer;

public class CurrentLoop {
    private int loop;

    public CurrentLoop(int initialValue) {
        loop = initialValue;
    }

    public void incValue() {
        loop += 1;
    }

    public int getValue() {
        return loop;
    }
}
