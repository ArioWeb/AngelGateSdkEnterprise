package com.angelsgate.sdk.AngelsGateUtils.WebSocketUp;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobManager;
import com.angelsgate.sdk.jobs.SocketJobs.LeftPartWSJob;


import java.util.concurrent.CountDownLatch;

public class LeftPartWSTask implements Runnable {
    private CountDownLatch completeSignal = new CountDownLatch(1);
    String handler;
    private String deviceId;

    Context ctx;


    public LeftPartWSTask(Context ctx, String handler, String deviceId ) {
        this.handler = handler;
        this.deviceId = deviceId;
        this.ctx = ctx;

    }


    @Override
    public void run() {
        LeftParts(handler, deviceId);
    }

    public void LeftParts(String handler, String deviceId) {

        JobManager jobmanager = new JobManager(ctx, WorkManager.getInstance());


        Data.Builder dataBuilder = LeftPartWSJob.constructData(handler, deviceId);

        final OneTimeWorkRequest Jobrequest = jobmanager.add(LeftPartWSJob.class, LeftPartWSJob.constructParameters(), dataBuilder);


        Handler handlerThrad = new Handler(Looper.getMainLooper());

        handlerThrad.post(new Runnable() {
            @Override
            public void run() {

                WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                        .observeForever(new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo info) {
                                if (info != null && info.getState().isFinished()) {
                                    String StatusResult = info.getOutputData().getString(LeftPartWSJob.KEY_RESULT);

                                    if (StatusResult != null) {


                                        switch (StatusResult) {

                                            case "success":
                                                String responseString = info.getOutputData().getString(LeftPartWSJob.KEY_RESPONSE);

                                                if (responseString.equals("NOTICE_FILE_PARTSEMPTY")) {

                                                } else {

                                                }
                                                completeSignal.countDown();
                                                break;
                                            case "Cancel":

                                                String errorMessage = info.getOutputData().getString(LeftPartWSJob.KEY_CANCEL_MESSAGE);
                                                completeSignal.countDown();
                                                break;


                                        }

                                    }


                                }
                            }
                        });


            }

        });


        try {
            completeSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
