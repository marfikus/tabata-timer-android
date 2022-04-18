package com.github.marfikus.tabatatimer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            TimeUnit.SECONDS.sleep(60 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }
}
