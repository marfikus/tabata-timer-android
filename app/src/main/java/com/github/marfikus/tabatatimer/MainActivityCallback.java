package com.github.marfikus.tabatatimer;

public interface MainActivityCallback {

    void updateInputFields(
            String workTime,
            String restTime,
            String loopCount,
            String startDelayTime
    );


}
