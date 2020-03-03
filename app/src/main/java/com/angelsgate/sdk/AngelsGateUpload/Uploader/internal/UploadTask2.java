package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal;

import android.content.Context;

import com.angelsgate.sdk.AngelsGateNetwork.model.file.SessionResponse;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Constants;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Priority;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Progress;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.UploadRequestStatus;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.UploadThreadStatus;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.core.UploadCore;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.Tasks.CheckFileTask;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.Tasks.CreateSessionTask;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.UploadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException.EXCEPTION_BASIC_ERROR;
import static com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException.EXCEPTION_SOME_THREADS_ERROR;
import static com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException.EXCEPTION_THREADS_NULL_ERROR;


public class UploadTask2 implements UploadThread.UploadProgressListener, CreateSessionTask.CreateSessionListener,CheckFileTask.CheckFileListener {

    ////////////////////////////////////////////////
    public final Priority priority;
    public final int sequence;
    public volatile UploadRequest request;
    Context context;
    //////////////////////////////////////////////
    private List<UploadThread> uploadThreads;
    private long lastRefreshTime = System.currentTimeMillis();
    private long progress;
    private volatile AtomicBoolean isComputeProgressUpload = new AtomicBoolean(false);
    private final UploadTaskListener uploadTaskListener;
    ///////////////////////////////////////
    private long timeout;
    private TimeUnit timeUnit;
    private static final int DEFAULT_TIMEOUT = 1;
    private static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.HOURS;
    private static final int THREAD_POOL_WAIT_TIME_IN_MILLIS = 5000;
    private ProgressHandler progressHandler;
    private List<Runnable> rejectedUploadThreads;


    UploadTask2(UploadRequest request, Context ctx, UploadTaskListener uploadTaskListener) {
        this.request = request;
        this.priority = request.getPriority();
        this.sequence = request.getSequenceNumber();
        this.context = ctx;
        this.uploadTaskListener = uploadTaskListener;
        this.uploadThreads = new ArrayList<>();
        this.rejectedUploadThreads = new ArrayList<>();
        timeout = DEFAULT_TIMEOUT;
        timeUnit = DEFAULT_TIMEUNIT;
    }


    public void start() {


        System.out.println("test UploadTask2  start");


        if (request.getOnProgressListener() != null) {
            progressHandler = new ProgressHandler(request.getOnProgressListener());
        }


        createUploadSession();


    }

    private void createUploadSession() {

        System.out.println("test UploadTask2  createUploadSession start");


        CreateSessionTask createUploadSessionTask = new CreateSessionTask(request, this, context);
        UploadCore.getInstance().getExecutorSupplier().forUploadTasks().submit(createUploadSessionTask);
    }


    @Override
    public void onSuccesscreateSession(SessionResponse Apiresponse) {


        System.out.println("test onSuccess createUploadSession  ");

        try {
            request.setHandler(Apiresponse.getHandler());
            request.setPartsize(Apiresponse.getPartsize());
            request.setTotalpart(Apiresponse.getTotalpart());


            System.out.println("test onSuccess createUploadSession   ");

            List<UploadThreadInfoModel> uploadThreadInfos = new ArrayList<>();


            for (int i = 0; i < Apiresponse.getTotalpart(); i++) {
                int j = i + 1;

                UploadThreadInfoModel uploadThreadInfo = new UploadThreadInfoModel(request.getUploadId() + j, request.getUploadId(), request.getHandler(),
                        j, Apiresponse.getPartsize(), 0, UploadThreadStatus.RUNNING.getType());
                uploadThreadInfos.add(uploadThreadInfo);


            }


            request.setUploadThreadInfos(uploadThreadInfos);
            request.setStatus(UploadRequestStatus.RUNNING.getType());

            UploadCore.getInstance().getExecutorSupplier().forUploadParts().setRejectedExecutionHandler(new RejectedExecutionPartsHandler());

            for (UploadThreadInfoModel uploadThreadInfo : uploadThreadInfos) {

                UploadThread uploadThread = new UploadThread(uploadThreadInfo, request, this, context );
                UploadCore.getInstance().getExecutorSupplier().forUploadParts().submit(uploadThread);
                uploadThreads.add(uploadThread);


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        } catch (UploadException e) {
            request.setStatus(UploadRequestStatus.ERROR.getType());
            request.handleException(e);
        }

    }


    @Override
    public void onFailedcreateSession(UploadException exception) {

        System.out.println("test onFailed createUploadSession " + exception.getCode());
        System.out.println("test onF createUploadSession " + exception.getMessage());


        if (request != null) {
            request.setStatus(UploadRequestStatus.ERROR.getType());
            request.handleException(exception);
        }

    }


    @Override
    public void onProgressUploadThread() {

        System.out.println("test onProgressUploadThread ");


        if (!isComputeProgressUpload.get()) {
            synchronized (this) {
                if (!isComputeProgressUpload.get()) {
                    isComputeProgressUpload.set(true);
                    long currentTimeMillis = System.currentTimeMillis();
                    if ((currentTimeMillis - lastRefreshTime) > 1000) {
                        computeUploadProgress();
                        request.onStatusChanged();
                        lastRefreshTime = currentTimeMillis;
                    }
                    isComputeProgressUpload.set(false);
                }
            }
        }
    }

    @Override
    public void onUploadSuccessUploadThread() {

        System.out.println("test onUploadSuccessUploadThread ");


        computeUploadProgress();

        System.out.println("test onUploadSuccessUploadThread complete " + request.getProgress());


        if (request.getProgress() >= request.getFilesize()) {

            boolean status = CheckRunningThreadAfterUpload();


            if (!status) {
                return;

            }


            ///////////chech file

            CheckFile();





        } else {
            CheckRunningThread();
        }


    }



    private void CheckFile() {

        System.out.println("test UploadTask2  CheckFile start");


        CheckFileTask CheckFileTask = new CheckFileTask(request, this, context);
        UploadCore.getInstance().getExecutorSupplier().forUploadTasks().submit(CheckFileTask);
    }


    @Override
    public void onSuccesscheckFile(String response) {

        System.out.println("test onSuccesscheckFile  response  " +response);


        switch (response) {
            case "NOTICE_FILE_COMPLETE":

                request.setStatus(UploadRequestStatus.COMPLETED.getType());
                if (uploadTaskListener != null) {
                    uploadTaskListener.onUploadSuccess(request, context);
                }

                break;

            case "NOTICE_FILE_ABORT":
                request.setStatus(UploadRequestStatus.ERROR.getType());
                request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "NOTICE_FILE_ABORT error ,please resume upload"));
                break;

            case "NOTICE_FILE_EXPIRE":
                request.setStatus(UploadRequestStatus.ERROR.getType());
                request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "NOTICE_FILE_EXPIRE error ,please resume upload"));
                break;


            case "NOTICE_FILE_PENDING":
                request.setStatus(UploadRequestStatus.ERROR.getType());
                request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "NOTICE_FILE_PENDING error ,please resume upload"));

                break;


            case "NOTICE_FILE_OTHER":
                request.setStatus(UploadRequestStatus.ERROR.getType());
                request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "NOTICE_FILE_OTHER error ,please resume upload"));
                break;
        }






    }

    @Override
    public void onFailedcheckFile(UploadException exception) {

        System.out.println("test onFailedcheckFile  ");


        request.setStatus(UploadRequestStatus.ERROR.getType());
        request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "checkFile error ,please resume upload"));

    }



    @Override
    public void onErrorUploadThread(String threadId, String uploadRequestId, int partNum) {

        System.out.println("test onErrorUploadThread " + partNum);


        request.onStatusChanged();
        CheckRunningThread();
    }

    public synchronized boolean CheckRunningThread() {

        System.out.println("test CheckRunningThread   ");

        if (uploadThreads != null) {


            System.out.println("test CheckRunningThread  uploadThreads.size()  " + uploadThreads.size());
            for (int i = 0; i < uploadThreads.size(); i++) {

                if (uploadThreads.get(i).getUploadThreadInfo().getUploadThreadStatus() == UploadThreadStatus.RUNNING.getType()) {

                    System.out.println("test CheckRunningThread   RUNNING RUNNING " + uploadThreads.get(i).getUploadThreadInfo().getPartNum());


                    return true;


                }
            }

            /////////////////run rejected thread

            if (rejectedUploadThreads != null && rejectedUploadThreads.size() > 0) {

                System.out.println("test CheckRunningThread   rejectedUploadThreads " + rejectedUploadThreads.size());

                for (int i = 0; i < rejectedUploadThreads.size(); i++) {

                    UploadCore.getInstance().getExecutorSupplier().forUploadParts().submit(rejectedUploadThreads.get(i));
                    uploadThreads.add((UploadThread) rejectedUploadThreads.get(i));
                    rejectedUploadThreads.remove(i);
                }

                return true;

            } else {

                ////////////check file
                System.out.println("test CheckRunningThread   rejectedUploadThreads  0000000");

                ////////check error thread
                for (int i = 0; i < uploadThreads.size(); i++) {

                    if (uploadThreads.get(i).getUploadThreadInfo().getUploadThreadStatus() == UploadThreadStatus.ERROR.getType()) {

                        System.out.println("test CheckRunningThread   ERROR  ERROR");

//                        closeAllSafely(outputStream);
                        request.setStatus(UploadRequestStatus.ERROR.getType());
                        request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "some thread error ,please resume upload"));
                        return false;
                    }
                }

                System.out.println("test CheckRunningThread   Basic thread error ,please resume upload");
                //////////

                request.setStatus(UploadRequestStatus.ERROR.getType());
                request.handleException(new UploadException(EXCEPTION_BASIC_ERROR, "Basic thread error ,please resume upload"));
                return false;


            }


        } else {

            System.out.println("test CheckRunningThread   EXCEPTION_THREADS_NULL_ERROR");

            request.setStatus(UploadRequestStatus.ERROR.getType());
            request.handleException(new UploadException(EXCEPTION_THREADS_NULL_ERROR, ""));
            return false;
        }

    }


    public synchronized boolean CheckRunningThreadAfterUpload() {

        System.out.println("test CheckRunningThreadAfterUpload   ");

        if (uploadThreads != null) {


            System.out.println("test CheckRunningThreadAfterUpload  uploadThreads.size()  " + uploadThreads.size());
            for (int i = 0; i < uploadThreads.size(); i++) {

                if (uploadThreads.get(i).getUploadThreadInfo().getUploadThreadStatus() == UploadThreadStatus.RUNNING.getType()) {

                    System.out.println("test CheckRunningThread   RUNNING RUNNING " + uploadThreads.get(i).getUploadThreadInfo().getPartNum());


                    return false;


                }
            }

            /////////////////run rejected thread

            if (rejectedUploadThreads != null && rejectedUploadThreads.size() > 0) {

                System.out.println("test CheckRunningThread   rejectedUploadThreads " + rejectedUploadThreads.size());

                for (int i = 0; i < rejectedUploadThreads.size(); i++) {

                    UploadCore.getInstance().getExecutorSupplier().forUploadParts().submit(rejectedUploadThreads.get(i));
                    uploadThreads.add((UploadThread) rejectedUploadThreads.get(i));
                    rejectedUploadThreads.remove(i);
                }

                return false;

            } else {

                ////////////check file
                System.out.println("test CheckRunningThread   rejectedUploadThreads  0000000");

                ////////check error thread
                for (int i = 0; i < uploadThreads.size(); i++) {

                    if (uploadThreads.get(i).getUploadThreadInfo().getUploadThreadStatus() == UploadThreadStatus.ERROR.getType()) {

                        System.out.println("test CheckRunningThread   ERROR  ERROR");

//                        closeAllSafely(outputStream);
                        request.setStatus(UploadRequestStatus.ERROR.getType());
                        request.handleException(new UploadException(EXCEPTION_SOME_THREADS_ERROR, "some thread error ,please resume upload"));
                        return false;
                    }
                }


                return true;


            }


        } else {

            System.out.println("test CheckRunningThread   EXCEPTION_THREADS_NULL_ERROR");

            request.setStatus(UploadRequestStatus.ERROR.getType());
            request.handleException(new UploadException(EXCEPTION_THREADS_NULL_ERROR, ""));
            return false;
        }

    }


    private void computeUploadProgress() {
        progress = 0;
        List<UploadThreadInfoModel> uploadThreadInfos = request.getUploadThreadInfos();
        for (UploadThreadInfoModel info : uploadThreadInfos) {
            progress += info.getProgress();
        }

        System.out.println("test computeUploadProgress  " + progress);


        request.setProgress(progress);
        if (progressHandler != null) {
            progressHandler
                    .obtainMessage(Constants.UPDATE,
                            new Progress(request.getProgress(),
                                    request.getFilesize())).sendToTarget();
        }

    }




    public interface UploadTaskListener {
        void onUploadSuccess(UploadRequest request, Context ctx);
    }


    class RejectedExecutionPartsHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

            UploadThread a = (UploadThread) runnable;
            System.out.println("test GetFileInfoTask  rejectedExecution " + a.getUploadThreadInfo().getPartNum());

            uploadThreads.remove((UploadThread) runnable);
            rejectedUploadThreads.add(runnable);
        }
    }


}
