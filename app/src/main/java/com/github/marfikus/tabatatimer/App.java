package com.github.marfikus.tabatatimer;

import android.app.Application;


public class App extends Application {
    public MainViewModel mainViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        mainViewModel = new MainViewModel(
                new AppSettings(getApplicationContext()),
                new SoundPlayer(getAssets()),
                new MyWakeLock(getApplicationContext()),
                new CurrentValues(
                        true,
                        R.string.start_button_start,
                        R.string.current_state_stopped,
                        0,
                        0,
                        false
                )
        );
    }
}
