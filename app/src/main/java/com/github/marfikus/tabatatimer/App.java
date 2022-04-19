package com.github.marfikus.tabatatimer;

import android.app.Application;

public class App extends Application {
    public MainViewModel mainViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        mainViewModel = new MainViewModel(
                new AppSettings(getApplicationContext())
        );
    }
}
