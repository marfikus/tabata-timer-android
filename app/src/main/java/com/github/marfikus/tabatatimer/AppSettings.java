package com.github.marfikus.tabatatimer;

import android.content.Context;
import android.content.SharedPreferences;


public class AppSettings {
    private SharedPreferences settings;
    private final String APP_SETTINGS = "TABATA_TIMER_SETTINGS";
    private final String WORK_TIME = "WORK_TIME";
    private final String REST_TIME = "REST_TIME";
    private final String LOOP_COUNT = "LOOP_COUNT";
    private final String START_DELAY_TIME = "START_DELAY_TIME";
    private final int WORK_TIME_DEFAULT = 20;
    private final int REST_TIME_DEFAULT = 10;
    private final int LOOP_COUNT_DEFAULT = 5;
    private final int START_DELAY_TIME_DEFAULT = 3;

    public AppSettings(Context context) {
        settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public int getWorkTime() {
        return settings.getInt(WORK_TIME, WORK_TIME_DEFAULT);
    }

    public void updateWorkTime(int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(WORK_TIME, value);
        editor.apply();
    }


    public int getRestTime() {
        return settings.getInt(REST_TIME, REST_TIME_DEFAULT);
    }

    public void updateRestTime(int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(REST_TIME, value);
        editor.apply();
    }


    public int getLoopCount() {
        return settings.getInt(LOOP_COUNT, LOOP_COUNT_DEFAULT);
    }

    public void updateLoopCount(int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LOOP_COUNT, value);
        editor.apply();
    }


    public int getStartDelayTime() {
        return settings.getInt(START_DELAY_TIME, START_DELAY_TIME_DEFAULT);
    }

    public void updateStartDelayTime(int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(START_DELAY_TIME, value);
        editor.apply();
    }
}
