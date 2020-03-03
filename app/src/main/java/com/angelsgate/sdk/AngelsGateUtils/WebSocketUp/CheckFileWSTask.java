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
import com.angelsgate.sdk.jobs.SocketJobs.CheckFileWSJob;


import java.util.concurrent.CountDownLatch;


public class CheckFileWSTask implements Runnable {
    private CountDownLatch completeSignal = new CountDownLatch(1);

    String handler;
    private String deviceId;

    Context ctx;


    public CheckFileWSTask(Context ctx, String handler, String deviceId ){
        this.handler = handler;
        this.deviceId = deviceId;
        this.ctx = ctx;
    }


    @Override
    public void run() {
        CheckFile(handler, deviceId);
    }

    public void CheckFile(String handler, String deviceId) {

        JobManager jobmanager = new JobManager(ctx, WorkManager.getInstance());

        Data.Builder dataBuilder = CheckFileWSJob.constructData(handler, deviceId);

        final OneTimeWorkRequest Jobrequest = jobmanager.add(CheckFileWSJob.class, CheckFileWSJob.constructParameters(), dataBuilder);


        Handler handlerThrad = new Handler(Looper.getMainLooper());

        handlerThrad.post(new Runnable() {
            @Override
            public void run() {

                WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                        .observeForever(new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo info) {
                                if (info != null && info.getState().isFinished()) {
                                    String StatusResult = info.getOutputData().getString(CheckFileWSJob.KEY_RESULT);

                                    if (StatusResult != null) {
                                        System.out.println("testJob CheckFileTask " + "StatusResult " + StatusResult);

                                        switch (StatusResult) {

                                            case "success":
                                                String responseString = info.getOutputData().getString(CheckFileWSJob.KEY_RESPONSE);



                                                switch (responseString) {
                                                    case "NOTICE_FILE_COMPLETE":
                                                        break;

                                                    case "NOTICE_FILE_ABORT":
                                                        break;

                                                    case "NOTICE_FILE_EXPIRE":
                                                        break;


                                                    case "NOTICE_FILE_PENDING":
                                                        break;


                                                    case "NOTICE_FILE_OTHER":
                                                        break;
                                                }

                                                completeSignal.countDown();
                                                break;
                                            case "Cancel":

                                                String errorMessage = info.getOutputData().getString(CheckFileWSJob.KEY_CANCEL_MESSAGE);
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
