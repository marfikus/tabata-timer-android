package com.github.marfikus.tabatatimer;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


/*
* Создаёт имитацию фоновой работы приложения, что не позволяет уснуть процессору
* во время работы таймера
*/
public class MyWakeLock {
    private final WorkManager workManager;
    private OneTimeWorkRequest workRequest;

    public MyWakeLock(Context context) {
        workManager = WorkManager.getInstance(context);
    }

    public void start(long timeoutInSeconds) {
        Data data = new Data.Builder()
                .putLong(Constants.WAKELOCK_TIMEOUT, timeoutInSeconds)
                .build();

        workRequest = new OneTimeWorkRequest.Builder(MyWakeLockWorker.class)
                .setInputData(data)
                .build();

        workManager.enqueue(workRequest);
    }

    public void stop() {
        workManager.cancelWorkById(workRequest.getId());
    }
}
