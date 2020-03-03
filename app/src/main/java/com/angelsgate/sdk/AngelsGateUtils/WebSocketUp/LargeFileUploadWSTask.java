package com.angelsgate.sdk.AngelsGateUtils.WebSocketUp;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.angelsgate.sdk.AngelsGateNetwork.model.file.FileUploadSessionPartRequest;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobManager;
import com.angelsgate.sdk.jobs.SocketJobs.PushPartWSJob;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

public class LargeFileUploadWSTask implements Runnable {

    private CountDownLatch completeSignal = new CountDownLatch(1);

    private final int partPostion;
    String handler;
    private List<FileUploadSessionPartRequest> parts;

    private long fileSize;
    private String deviceId;
    private ThreadPoolExecutor executorService;
    Context ctx;


    /**
     * Runable task to create parallel http connctions for file upload session.
     *
     * @param handler     file upload session object
     * @param fileSize    total file size
     * @param parts       list of the BoxFileUploadSessionPart objects
     * @param partPostion sequence number of the part
     */


    public LargeFileUploadWSTask(Context ctx, String handler,
                                 long fileSize, List<FileUploadSessionPartRequest> parts, int partPostion, String deviceId, ThreadPoolExecutor executorService) {
        this.handler = handler;
        this.fileSize = fileSize;
        this.parts = parts;
        this.partPostion = partPostion;
        this.deviceId = deviceId;
        this.executorService = executorService;
        this.ctx = ctx;
    }


    @Override
    public void run() {

        PushPart(handler, partPostion,  deviceId);

    }


    public synchronized void PushPart(final String handler, final int partNumber,  String deviceId) {

        JobManager jobmanager = new JobManager(ctx, WorkManager.getInstance());

        Data.Builder dataBuilder = PushPartWSJob.constructData(handler, partNumber,   deviceId);


        final OneTimeWorkRequest Jobrequest = jobmanager.add(PushPartWSJob.class, PushPartWSJob.constructParameters(), dataBuilder);




        Handler handlerThrad = new Handler(Looper.getMainLooper());

        handlerThrad.post(new Runnable() {
            @Override
            public void run() {


                WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                        .observeForever(new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo info) {
                                if (info != null && info.getState().isFinished()) {
                                    String StatusResult = info.getOutputData().getString(PushPartWSJob.KEY_RESULT);

                                    if (StatusResult != null) {


                                        switch (StatusResult) {

                                            case "success":


                                                String responseString = info.getOutputData().getString(PushPartWSJob.KEY_RESPONSE);


                                                switch (responseString) {
                                                    case "NOTICE_FILE_UPLOADDONE":
                                                        break;

                                                    case "NOTICE_FILE_COMPLETE":
                                                        if (executorService != null && !executorService.isShutdown()) {
                                                            executorService.shutdownNow();
                                                        }
                                                        break;

                                                    case "NOTICE_FILE_ABORT":

                                                        if (executorService != null && !executorService.isShutdown()) {
                                                            executorService.shutdownNow();
                                                        }
                                                        break;

                                                    case "NOTICE_FILE_EXPIRE":
                                                        if (executorService != null && !executorService.isShutdown()) {
                                                            executorService.shutdownNow();
                                                        }
                                                        break;


                                                    case "ERROR_FILE_NOTFOUND":

                                                        if (executorService != null && !executorService.isShutdown()) {
                                                            executorService.shutdownNow();
                                                        }
                                                        break;


                                                }


                                                FileUploadSessionPartRequest part = null;

                                                completeSignal.countDown();

                                                break;

                                            case "Cancel":

                                                String errorMessage = info.getOutputData().getString(PushPartWSJob.KEY_CANCEL_MESSAGE);



                                                if (executorService != null && !executorService.isShutdown()) {
                                                    executorService.shutdownNow();
                                                }

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
