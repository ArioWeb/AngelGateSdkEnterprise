package com.angelsgate.sdk.AngelsGateDownload.downloader.internal;

import android.content.Context;
import android.os.Process;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadRequestStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadThreadStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Utils.Utils;
import com.angelsgate.sdk.AngelsGateDownload.downloader.database.DownloadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadPauseException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.FileStream.FileDownloadOutputStream;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.FileStream.FileDownloadRandomAccessFile;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.download.GetFilePartRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.download.GetFilePartResponse;
import com.angelsgate.sdk.AngelsGateUtils.Base64Utils;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadThread implements Runnable {

    private final DownloadThreadInfoModel downloadThreadInfo;
    private volatile DownloadRequest request;
    private final DownloadProgressListener downloadProgressListener;
    private int retryDownloadCount;
    private final int partNum;
    private final long partSize;
    private final String deviceId;
    Context ctx;
    FileDownloadOutputStream outputStream = null;
    private static final long TIME_GAP_FOR_SYNC = 1000;
    private static final long MIN_BYTES_FOR_SYNC = 65536;
    private long lastSyncTime;
    private long lastSyncBytes;
    int totalParts;


    //FileUploadInputStream outputStream

    public DownloadThread(DownloadThreadInfoModel downloadThreadInfo,
                          DownloadRequest request, DownloadProgressListener downloadProgressListener,
                          Context ctx, int totalParts) {
        this.downloadThreadInfo = downloadThreadInfo;
        this.request = request;
        this.downloadProgressListener = downloadProgressListener;
        this.partNum = downloadThreadInfo.getPartNum();
        this.partSize = downloadThreadInfo.getPartSize();
        this.deviceId = request.getDeviceId();
        this.ctx = ctx;
//        this.outputStream = outputStream;
        this.retryDownloadCount = 0;
        this.totalParts = totalParts;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);


        checkPause();

        try {
            GetFilePart(request.getHandler(), partNum, deviceId);
        } catch (DownloadException e) {


            System.out.println("test  UploadThread UploadException  " + partNum + " " + e.getCode() + "   " + e.getMessage());

            if (retryDownloadCount >= ComponentHolder.getInstance().getRetryDownloadThreadCount()) {
                retryDownloadCount = 0;
                downloadThreadInfo.setDownloadThreadStatus(DownloadThreadStatus.ERROR.getType());
                downloadProgressListener.onErrorDownloadThread(downloadThreadInfo.getThreadId(), downloadThreadInfo.getDownloadRequestId(), downloadThreadInfo.getPartNum());
                return;

            }

            retryDownloadCount++;
            run();

        }

    }


    private void checkPause() {
        if (request.getStatus() == DownloadRequestStatus.PAUSED.getType()) {
            throw new DownloadPauseException(DownloadException.EXCEPTION_PAUSE);
        }
    }


    public interface DownloadProgressListener {

        void onProgressDownloadThread();

        void onDownloadSuccessDownloadThread();

        void onErrorDownloadThread(String threadId, String downloadRequestId, int partNum);

    }


    public void GetFilePart(final String handler, final int partNumber, String deviceId) {


        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(ctx);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "GetFilePart";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;


        GetFilePartRequest input = new GetFilePartRequest(handler, String.valueOf(partNumber));

        Call<ResponseBody> callback = ComponentHolder.getInstance().getApiInterface().GetFilePart(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, input);
        Response<ResponseBody> response = null;
        try {
            response = callback.execute();
        } catch (IOException e) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "execute callback failed", e);

        }


        if (response.body() != null && response.isSuccessful()) {


            String bodyResponse = null;
            try {
                bodyResponse = response.body().string();

            } catch (IOException e) {
                throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "bodyResponse null", e);
            }
            // try {

            System.out.println("test  UploadThread GetFilePart start33333  " + partNumber);
            if (AngelsGate.StringErroreHandler(bodyResponse)) {

                String data_response = null;
                try {
                    data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, ctx);
                } catch (GeneralSecurityException e) {
                    throw new DownloadException(DownloadException.EXCEPTION_PROTOCOL, "Decode Error", e);
                }

                System.out.println("test  UploadThread GetFilePart start44444  " + partNumber);


                if (AngelsGate.ErroreHandler(data_response)) {

                    if (LocalErrorHandler(data_response)) {


                        Gson convertor = new Gson();
                        GetFilePartResponse GetFilePartApiResponseObject = convertor.fromJson(data_response, GetFilePartResponse.class);


                        synchronized (DownloadThread.this) {
                            try {
//////////////////////
                                String tempPath = Utils.getTempPath(request.getDirPath(), request.getFileName(), request.getHandler(), request.getFileExtention());

                                File file = new File(tempPath);


                                try {
                                    outputStream = FileDownloadRandomAccessFile.create(file);
                                } catch (IOException e) {
                                    throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "create RandomAccessFile failed", e);
                                }

////////////////////////

                                if (outputStream != null) {
                                    outputStream.seek((partNumber - 1) * downloadThreadInfo.getPartSize());

                                    System.out.println("test outputStream.seek " + partNumber );

                                }

                            } catch (IOException e) {

                                throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "set seek failed", e);

                            } catch (IllegalAccessException e) {

                                throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "set seek failed", e);

                            }


                            try {
                                if (outputStream != null) {

                                    byte[] bytes = Base64Utils.Base64DecodeToByteForDownload(GetFilePartApiResponseObject.getData());

                                    byte[] result = EncodeAlgorithmUtils.InflateForDownload(bytes);


                                    try {
                                        byte[] digestBytes = MessageDigest.getInstance("MD5").digest(GetFilePartApiResponseObject.getData().getBytes());

                                        String partCheckSum = EncodeAlgorithmUtils.bytesToHex(digestBytes);


                                        if (!partCheckSum.equals(GetFilePartApiResponseObject.getChecksum())) {

                                            System.out.println("test write part not check sum " + partNumber);
                                        }
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }

                                    System.out.println("test outputStream.write " + partNumber);
                                    outputStream.write(result);
                                }


                            } catch (IOException e) {

                                throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "write file failed", e);
                            }


                            if (outputStream != null)
                                syncIfRequired(outputStream);
                        }

                        if(request.getProgress() >=request.getTotalBytes() ){

                            System.out.println("test rename UploadThread GetFilePart  setProgress " + partNumber);
                            System.out.println("test rename  UploadThread GetFilePart  setProgress " + request.getProgress());
                            System.out.println("test rename UploadThread GetFilePart  setProgress " + request.getTotalBytes());

                        }




                        /////////////
                        System.out.println("test succ111 download thread " + partNumber);
                        downloadThreadInfo.setProgress(downloadThreadInfo.getPartSize());
                        downloadThreadInfo.setDownloadThreadStatus(DownloadThreadStatus.COMPLETED.getType());
                        downloadProgressListener.onProgressDownloadThread();
                        downloadProgressListener.onDownloadSuccessDownloadThread();
                        System.out.println("test succ222 download thread " + partNumber);


                    } else {
                        throw new DownloadException(DownloadException.EXCEPTION_PROTOCOL, "Data Incorect");
                    }


                } else {
                    throw new DownloadException(DownloadException.EXCEPTION_PROTOCOL, "Error in data sended");
                }

            } else {
                throw new DownloadException(DownloadException.EXCEPTION_PROTOCOL, "Error in first data sended ");

            }


        } else {
            System.out.println("test threadError response.code()" + response.code());

            System.out.println("test threadError response.errorBody()" + response.errorBody());


            throw new DownloadException(DownloadException.EXCEPTION_CONNECTION_ERROR, "Error Connect to Server");
        }


    }

    private void syncIfRequired(FileDownloadOutputStream outputStream) {

//        final long currentTime = System.currentTimeMillis();
//
//        final long timeDelta = currentTime - lastSyncTime;
//        if (timeDelta > TIME_GAP_FOR_SYNC) {
            sync(outputStream);
           // lastSyncTime = currentTime;
      //  }

    }

    private void sync(FileDownloadOutputStream outputStream) {

        try {
            outputStream.flushAndSync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputStream=null;
        System.gc();
    }


    public static boolean LocalErrorHandler(String respose) {

        switch (respose) {
            case "ERROR_FILE_NOTFOUND":
                return false;

            case "ERROR_FILE_OWNERNOTMATCH":
                return false;


            case "ERROR_FILE_WRONGPARTNUM":
                return false;


            default:
                return true;
        }
    }

    public DownloadThreadInfoModel getDownloadThreadInfo() {
        return downloadThreadInfo;
    }


}
