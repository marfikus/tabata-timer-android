package com.github.marfikus.tabatatimer;

import android.content.Context;
import android.util.Log;

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
            long totalTime = getInputData().getLong("TOTAL_TIME", 60 * 5);
//            Log.d("Worker", "totalTime: " + totalTime);
            TimeUnit.SECONDS.sleep(totalTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }
}
