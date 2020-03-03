package com.angelsgate.sdk.AngelsGateDownload.downloader.internal;

import android.content.Context;
import android.os.AsyncTask;

import com.angelsgate.sdk.AngelsGateDownload.downloader.Constants;
import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadRequestStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadThreadStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Priority;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Progress;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Utils.Utils;
import com.angelsgate.sdk.AngelsGateDownload.downloader.core.DownloadCore;
import com.angelsgate.sdk.AngelsGateDownload.downloader.database.DownloadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.FileStream.FileDownloadOutputStream;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.FileStream.FileDownloadRandomAccessFile;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.download.GetFileInfoResponse;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException.EXCEPTION_BASIC_ERROR;
import static com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException.EXCEPTION_IO_EXCEPTION;
import static com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException.EXCEPTION_SOME_THREADS_ERROR;
import static com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException.EXCEPTION_THREADS_NULL_ERROR;

public class DownloadTask2 implements DownloadThread.DownloadProgressListener, GetFileInfoTask.OnGetFileInfoListener {

    ////////////////////////////////////////////////
    public final Priority priority;
    public final int sequence;
    public volatile  DownloadRequest request;
    Context context;
    //////////////////////////////////////////////
    private List<DownloadThread> downloadThreads;
    private long lastRefreshTime = System.currentTimeMillis();
    private long progress;
    private volatile AtomicBoolean isComputeProgressDownload = new AtomicBoolean(false);
    private final DownloadTaskListener downloadTaskListener;
    ///////////////////////////////////////
    private long timeout;
    private TimeUnit timeUnit;
    private static final int DEFAULT_TIMEOUT = 1;
    private static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.HOURS;
    private static final int THREAD_POOL_WAIT_TIME_IN_MILLIS = 5000;
    private ProgressHandler progressHandler;
//    FileUploadInputStream outputStream = null;
    private List<Runnable> rejectedDownloadThreads;
    ////////////
    String checksumFile = "";


    DownloadTask2(DownloadRequest request, Context ctx, DownloadTaskListener downloadTaskListener) {
        this.request = request;
        this.priority = request.getPriority();
        this.sequence = request.getSequenceNumber();
        this.context = ctx;
        this.downloadTaskListener = downloadTaskListener;
        this.downloadThreads = new ArrayList<>();
        this.rejectedDownloadThreads = new ArrayList<>();
        timeout = DEFAULT_TIMEOUT;
        timeUnit = DEFAULT_TIMEUNIT;
    }


    public void start() {


        System.out.println("test UploadTask2  start");


        if (request.getOnProgressListener() != null) {
            progressHandler = new ProgressHandler(request.getOnProgressListener());
        }


        if (request.getProgress() <= 0) {

            System.out.println("test UploadTask2  getProgress 0");

            getFileInfo();

        } else {

            System.out.println("test UploadTask2  getProgress not 0");

            getCurrentFileInfo();

            DownloadCore.getInstance().getExecutorSupplier().forDownloadParts().setRejectedExecutionHandler(new RejectedExecutionPartsHandler());

            List<DownloadThreadInfoModel> downloadThreadInfos = request.getDownloadThreadInfos();

            System.out.println("test UploadTask2  downloadThreadInfos");


            for (DownloadThreadInfoModel downloadThreadInfo : downloadThreadInfos) {


                if (!(downloadThreadInfo.getDownloadThreadStatus() == DownloadThreadStatus.COMPLETED.getType())) {

                    int a = (int) (request.getTotalBytes() / downloadThreadInfo.getPartSize());

                    long b = (int) (request.getTotalBytes() % downloadThreadInfo.getPartSize());

                    int c = 0;
                    if (b == 0) {

                        c = a;
                    } else {

                        c = a + 1;
                    }

                    DownloadThread downloadThread = new DownloadThread(downloadThreadInfo, request, this, context,   c);
                    DownloadCore.getInstance().getExecutorSupplier().forDownloadParts().submit(downloadThread);
                    downloadThreads.add(downloadThread);

                }

            }

            request.setStatus(DownloadRequestStatus.RUNNING.getType());


        }


    }

    private void getFileInfo() {

        System.out.println("test UploadTask2  getFileInfo start");


        GetFileInfoTask getFileInfoTask = new GetFileInfoTask(request, this, context);
        DownloadCore.getInstance().getExecutorSupplier().forDownloadTasks().submit(getFileInfoTask);
    }

    public void getCurrentFileInfo() {

        String tempPath = Utils.getTempPath(request.getDirPath(), request.getFileName(), request.getHandler(), request.getFileExtention());

        File file = new File(tempPath);

        System.out.println("test UploadTask2  getCurrentFileInfo");


        if (!file.exists()) {

            System.out.println("test UploadTask2  getCurrentFileInfo !file.exists()");
            //////reset colan  az hame ja
            request.setTotalBytes(0);
            request.setProgress(0);
            request.setStatus(1);
            request.setDownloadThreadInfos(new ArrayList<DownloadThreadInfoModel>());
            DownloadCore.getInstance().getExecutorSupplier().forBackgroundTasks().submit(new Runnable() {
                @Override
                public void run() {
                    ComponentHolder.getInstance().getDbHelper().removeDownloadThread(request.getDownloadId());
                }
            });

            ////
            getFileInfo();
            return;
        }


//        try {
//            outputStream = FileUploadRandomAccessFile.create(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public void onSuccessGetFileInfo(GetFileInfoResponse Apiresponse, boolean isSupportRanges) {

        checksumFile = Apiresponse.getChecksum();




        System.out.println("test onSuccessGetFileInfo  ");

        try {
            request.setFileExtention(Apiresponse.getExtension());
            request.setTotalBytes(Apiresponse.getSize());

            String tempPath = Utils.getTempPath(request.getDirPath(), request.getFileName(), request.getHandler(), request.getFileExtention());
            File file = new File(tempPath);


            if (!file.exists()) {

                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new DownloadException(EXCEPTION_IO_EXCEPTION, "Error in createNewFile");



                }

                System.out.println("test onSuccessGetFileInfo  createNewFile");

            }


//            try {
//                outputStream = FileUploadRandomAccessFile.create(file);
//            } catch (IOException e) {
//                System.out.println("test outputStream " + e.getMessage());
//                System.out.println("test outputStream " + e.getCause());
//
//                throw new UploadException(EXCEPTION_IO_EXCEPTION, "Error in outputStream from File");
//            }

            System.out.println("test onSuccessGetFileInfo  outputStream");

            List<DownloadThreadInfoModel> downloadThreadInfos = new ArrayList<>();

            if (isSupportRanges) {

                for (int i = 0; i < Apiresponse.getPartnum(); i++) {
                    int j = i + 1;

                    DownloadThreadInfoModel downloadThreadInfo = new DownloadThreadInfoModel(request.getDownloadId() + j, request.getDownloadId(), request.getHandler(),
                            j, Apiresponse.getPartsize(), 0, DownloadThreadStatus.RUNNING.getType());
                    downloadThreadInfos.add(downloadThreadInfo);


                }


                System.out.println("test onSuccessGetFileInfo  isSupportRanges after DownloadThreadInfoModel");

            } else {

                DownloadThreadInfoModel downloadThreadInfo = new DownloadThreadInfoModel(request.getDownloadId() + 1, request.getDownloadId(), request.getHandler(),
                        1, Apiresponse.getPartsize(), 0, DownloadThreadStatus.RUNNING.getType());
                downloadThreadInfos.add(downloadThreadInfo);

            }

            request.setDownloadThreadInfos(downloadThreadInfos);
            request.setStatus(DownloadRequestStatus.RUNNING.getType());

            DownloadCore.getInstance().getExecutorSupplier().forDownloadParts().setRejectedExecutionHandler(new RejectedExecutionPartsHandler());

            for (DownloadThreadInfoModel downloadThreadInfo : downloadThreadInfos) {

                DownloadThread downloadThread = new DownloadThread(downloadThreadInfo, request, this, context,  Apiresponse.getPartnum());
                DownloadCore.getInstance().getExecutorSupplier().forDownloadParts().submit(downloadThread);
                downloadThreads.add(downloadThread);


//                while (UploadCore.getInstance().getExecutorSupplier().forDownloadParts() != null
//                        && (UploadCore.getInstance().getExecutorSupplier().forDownloadParts().getTaskCount() - UploadCore.getInstance().getExecutorSupplier().forDownloadParts().getCompletedTaskCount() > 100)) {
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        } catch (DownloadException e) {
            request.setStatus(DownloadRequestStatus.ERROR.getType());
            request.handleException(e);
        }

    }


    @Override
    public void onFailedGetFileInfo(DownloadException exception) {

        System.out.println("test onFailedGetFileInfo " + exception.getCode());
        System.out.println("test onFailedGetFileInfo " + exception.getMessage());


        if (request != null) {
            request.setStatus(DownloadRequestStatus.ERROR.getType());
            request.handleException(exception);
        }

    }


    @Override
    public void onProgressDownloadThread() {

        System.out.println("test onProgressDownloadThread ");


        if (!isComputeProgressDownload.get()) {
            synchronized (this) {
                if (!isComputeProgressDownload.get()) {
                    isComputeProgressDownload.set(true);
                    long currentTimeMillis = System.currentTimeMillis();
                    if ((currentTimeMillis - lastRefreshTime) > 1000) {
                        computeDownloadProgress();
                        request.onStatusChanged();
                        lastRefreshTime = currentTimeMillis;
                    }
                    isComputeProgressDownload.set(false);
                }
            }
        }
    }

    @Override
    public void onDownloadSuccessDownloadThread() {

        System.out.println("test onDownloadSuccessDownloadThread ");


        computeDownloadProgress();

        System.out.println("test onDownloadSuccessDownloadThread complete " + request.getProgress());


        if (request.getProgress() >= request.getTotalBytes()) {

           boolean status= CheckRunningThreadAfterDownload();


           if(!status){
               return;

           }
//            try {
//                outputStream.flushAndSync();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //////////////////////

            final String path = Utils.getPath(request.getDirPath(), request.getFileName(), request.getHandler(), request.getFileExtention());
            String tempPath = Utils.getTempPath(request.getDirPath(), request.getFileName(), request.getHandler(), request.getFileExtention());

            try {
                System.out.println("test rename "+request.getProgress());
                System.out.println("test rename "+request.getTotalBytes());



                Utils.renameFileName(tempPath, path);

                System.out.println("test onDownloadSuccessDownloadThread renameFileName ");

            } catch (IOException e) {
                request.setStatus(DownloadRequestStatus.ERROR.getType());
                request.handleException(new DownloadException(EXCEPTION_IO_EXCEPTION, "renameFile Failed"));
            }

            removeNoMoreNeededModelFromDatabase();
//            closeAllSafely(outputStream);

            request.setStatus(DownloadRequestStatus.COMPLETED.getType());
            if (downloadTaskListener != null) {
                downloadTaskListener.onDownloadSuccess(request, context);
            }


            new checksumTask().execute();
        } else {
            CheckRunningThread();
        }


    }


    ///////////////////////////////////
    class checksumTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }


        protected Void doInBackground(Void... inputData) {


            String path = Utils.getPath(request.getDirPath(), request.getFileName(), request.getHandler(), request.getFileExtention());

            InputStream is = null;
            try {
                is = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            File file = new File(path);

            System.out.println("test checksumTask file.length() " + file.length());


////////////////////////create checksum file
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ae) {

            }


            byte[] buffer = new byte[2048];
            int read = 0;
            try {
                while (true) {
                    try {
                        if (!((read = is.read(buffer)) > 0)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    digest.update(buffer, 0, read);
                }
            } catch (Exception e) {
            }


            //Create  the file hash
            byte[] digestBytes = digest.digest();

            String fileCheckSum = EncodeAlgorithmUtils.bytesToHex(digestBytes);
            System.out.println("TEST checksumTask fileCheckSum 0: " + fileCheckSum);

            if (fileCheckSum.length() > 32) {
                fileCheckSum = fileCheckSum.substring(0, 32);
            }


            System.out.println("TEST checksumTask fileCheckSum 1: " + fileCheckSum);

            System.out.println("TEST checksumTask fileCheckSum serverrrr: " + checksumFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }


    }

    //////////////////////


    @Override
    public void onErrorDownloadThread(String threadId, String downloadRequestId, int partNum) {

        System.out.println("test onErrorDownloadThread " + partNum);


        request.onStatusChanged();
        CheckRunningThread();
    }

    public synchronized boolean CheckRunningThread() {

        System.out.println("test CheckRunningThread   ");

        if (downloadThreads != null) {


            System.out.println("test CheckRunningThread  downloadThreads.size()  "+downloadThreads.size());
            for (int i = 0; i < downloadThreads.size(); i++) {

                if (downloadThreads.get(i).getDownloadThreadInfo().getDownloadThreadStatus() == DownloadThreadStatus.RUNNING.getType()) {

                    System.out.println("test CheckRunningThread   RUNNING RUNNING " + downloadThreads.get(i).getDownloadThreadInfo().getPartNum());


                    return true;


                }
            }

            /////////////////run rejected thread

            if (rejectedDownloadThreads != null && rejectedDownloadThreads.size() > 0) {

                System.out.println("test CheckRunningThread   rejectedDownloadThreads " + rejectedDownloadThreads.size());

                for (int i = 0; i < rejectedDownloadThreads.size(); i++) {

                    DownloadCore.getInstance().getExecutorSupplier().forDownloadParts().submit(rejectedDownloadThreads.get(i));
                    downloadThreads.add((DownloadThread) rejectedDownloadThreads.get(i));
                    rejectedDownloadThreads.remove(i);
                }

                return true;

            } else {
                System.out.println("test CheckRunningThread   rejectedDownloadThreads  0000000");

                ////////check error thread
                for (int i = 0; i < downloadThreads.size(); i++) {

                    if (downloadThreads.get(i).getDownloadThreadInfo().getDownloadThreadStatus() == DownloadThreadStatus.ERROR.getType()) {

                        System.out.println("test CheckRunningThread   ERROR  ERROR");

//                        closeAllSafely(outputStream);
                        request.setStatus(DownloadRequestStatus.ERROR.getType());
                        request.handleException(new DownloadException(EXCEPTION_SOME_THREADS_ERROR, "some thread error ,please resume download"));
                        return false;
                    }
                }

                System.out.println("test CheckRunningThread   Basic thread error ,please resume download");
                //////////
//                closeAllSafely(outputStream);
                request.setStatus(DownloadRequestStatus.ERROR.getType());
                request.handleException(new DownloadException(EXCEPTION_BASIC_ERROR, "Basic thread error ,please resume download"));
                return false;


            }


        } else {

            System.out.println("test CheckRunningThread   EXCEPTION_THREADS_NULL_ERROR");

//            closeAllSafely(outputStream);
            request.setStatus(DownloadRequestStatus.ERROR.getType());
            request.handleException(new DownloadException(EXCEPTION_THREADS_NULL_ERROR, ""));
            return false;
        }

    }



    public synchronized boolean CheckRunningThreadAfterDownload() {

        System.out.println("test CheckRunningThreadAfterDownload   ");

        if (downloadThreads != null) {


            System.out.println("test CheckRunningThreadAfterDownload  downloadThreads.size()  "+downloadThreads.size());
            for (int i = 0; i < downloadThreads.size(); i++) {

                if (downloadThreads.get(i).getDownloadThreadInfo().getDownloadThreadStatus() == DownloadThreadStatus.RUNNING.getType()) {

                    System.out.println("test CheckRunningThread   RUNNING RUNNING " + downloadThreads.get(i).getDownloadThreadInfo().getPartNum());


                    return false;


                }
            }

            /////////////////run rejected thread

            if (rejectedDownloadThreads != null && rejectedDownloadThreads.size() > 0) {

                System.out.println("test CheckRunningThread   rejectedDownloadThreads " + rejectedDownloadThreads.size());

                for (int i = 0; i < rejectedDownloadThreads.size(); i++) {

                    DownloadCore.getInstance().getExecutorSupplier().forDownloadParts().submit(rejectedDownloadThreads.get(i));
                    downloadThreads.add((DownloadThread) rejectedDownloadThreads.get(i));
                    rejectedDownloadThreads.remove(i);
                }

                return false;

            } else {
                System.out.println("test CheckRunningThread   rejectedDownloadThreads  0000000");

                ////////check error thread
                for (int i = 0; i < downloadThreads.size(); i++) {

                    if (downloadThreads.get(i).getDownloadThreadInfo().getDownloadThreadStatus() == DownloadThreadStatus.ERROR.getType()) {

                        System.out.println("test CheckRunningThread   ERROR  ERROR");

//                        closeAllSafely(outputStream);
                        request.setStatus(DownloadRequestStatus.ERROR.getType());
                        request.handleException(new DownloadException(EXCEPTION_SOME_THREADS_ERROR, "some thread error ,please resume download"));
                        return false;
                    }
                }


                return true;


            }


        } else {

            System.out.println("test CheckRunningThread   EXCEPTION_THREADS_NULL_ERROR");

//            closeAllSafely(outputStream);
            request.setStatus(DownloadRequestStatus.ERROR.getType());
            request.handleException(new DownloadException(EXCEPTION_THREADS_NULL_ERROR, ""));
            return false;
        }

    }



    private void computeDownloadProgress() {
        progress = 0;
        List<DownloadThreadInfoModel> downloadThreadInfos = request.getDownloadThreadInfos();
        for (DownloadThreadInfoModel info : downloadThreadInfos) {
            progress += info.getProgress();
        }

        System.out.println("test computeDownloadProgress  " + progress);


        request.setProgress(progress);
        if (progressHandler != null) {
            progressHandler
                    .obtainMessage(Constants.UPDATE,
                            new Progress(request.getProgress(),
                                    request.getTotalBytes())).sendToTarget();
        }

    }

    private void closeAllSafely(FileDownloadOutputStream outputStream) {

        if (outputStream != null)
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


    private void removeNoMoreNeededModelFromDatabase() {
        ComponentHolder.getInstance().getDbHelper().removeDownloadRequest(request.getDownloadId());
    }

    public interface DownloadTaskListener {
        void onDownloadSuccess(DownloadRequest request, Context ctx);
    }


    class RejectedExecutionPartsHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

            DownloadThread a = (DownloadThread) runnable;
            System.out.println("test GetFileInfoTask  rejectedExecution " + a.getDownloadThreadInfo().getPartNum());

            downloadThreads.remove((DownloadThread) runnable);
            rejectedDownloadThreads.add(runnable);
        }
    }


}
