package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal;

import android.content.Context;
import android.os.Process;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.FileUploadSessionPartRequest;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.UploadThreadStatus;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.FileStream.FileUploadInputStream;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.FileStream.FileUploadRandomAccessFile;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.UploadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;
import com.angelsgate.sdk.AngelsGateUtils.Base64Utils;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadThread implements Runnable {

    private final UploadThreadInfoModel uploadThreadInfo;
    private volatile UploadRequest request;
    private final UploadProgressListener uploadProgressListener;
    private int retryUploadCount;
    private final int partNum;
    private final long partSize;
    private final String deviceId;
    Context ctx;
    FileUploadInputStream inputStream = null;


    public UploadThread(UploadThreadInfoModel uploadThreadInfo,
                        UploadRequest request, UploadProgressListener uploadProgressListener,
                        Context ctx) {
        this.uploadThreadInfo = uploadThreadInfo;
        this.request = request;
        this.uploadProgressListener = uploadProgressListener;
        this.partNum = uploadThreadInfo.getPartNum();
        this.partSize = uploadThreadInfo.getPartSize();
        this.deviceId = request.getDeviceId();
        this.ctx = ctx;
        this.retryUploadCount = 0;

    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            PushPart();
        } catch (UploadException e) {


            System.out.println("test  UploadThread UploadException  " + partNum + " " + e.getCode() + "   " + e.getMessage());

            if (retryUploadCount >= ComponentHolder.getInstance().getRetryUploadThreadCount()) {
                retryUploadCount = 0;
                uploadThreadInfo.setUploadThreadStatus(UploadThreadStatus.ERROR.getType());
                uploadProgressListener.onErrorUploadThread(uploadThreadInfo.getThreadId(), uploadThreadInfo.getUploadRequestId(), uploadThreadInfo.getPartNum());
                return;

            }

            retryUploadCount++;
            run();

        }

    }


    public interface UploadProgressListener {

        void onProgressUploadThread();

        void onUploadSuccessUploadThread();

        void onErrorUploadThread(String threadId, String uploadRequestId, int partNum);

    }


    public void PushPart() {

        String part_data = "";
        ////////////////////////

        synchronized (UploadThread.this) {
            try {
                File file = new File(request.getSelectedPath());


                try {
                    inputStream = FileUploadRandomAccessFile.create(file);
                } catch (IOException e) {
                    throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "create RandomAccessFile failed", e);
                }

////////////////////////

                if (inputStream != null) {
                    inputStream.seek((partNum - 1) * uploadThreadInfo.getPartSize());

                    System.out.println("test inputStream.seek " + partNum);

                }

            } catch (IOException e) {

                throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "set seek failed", e);

            } catch (IllegalAccessException e) {

                throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "set seek failed", e);

            }


            if (inputStream != null) {


                long tempPartSize;

                if (partNum == request.getTotalpart()) {

                    long diff = request.getFilesize() - ((partNum - 1) * uploadThreadInfo.getPartSize());
                    //The size last part of the file can be lesser than the part size.
                    if (diff < partSize) {
                        tempPartSize = diff;
                    } else {

                        tempPartSize = partSize;
                    }


                } else {
                    tempPartSize = partSize;
                }


                byte[] bytes = new byte[(int) tempPartSize];
                try {
                    int readStatus = inputStream.read(bytes);

                    if (readStatus == -1) {

                        System.out.println("test  throw Stream ended while upload was progressing");
                    }

                } catch (IOException ioe) {
                    throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "write file failed", ioe);
                }



                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    inputStream=null;
                    System.gc();
                }



                byte[] result = EncodeAlgorithmUtils.Deflate(bytes);
                String dataBase64 = Base64Utils.Base64Encode(result);

                part_data = dataBase64;

            }


        }


        System.out.println("test pushdata start");
        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(ctx);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "PushPart";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;

        String checksum = null;
        try {
            checksum = EncodeAlgorithmUtils.md5(part_data);
        } catch (UnsupportedEncodingException e) {
            throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "checksum failed");
        }


        if (part_data == null && part_data.length() <= 0) {
            throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "part_data failed");
        }


        FileUploadSessionPartRequest input = new FileUploadSessionPartRequest(request.getHandler(), partNum, part_data, checksum);

        Call<ResponseBody> callback = ComponentHolder.getInstance().getApiInterface().PushPart(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, input);
        Response<ResponseBody> response = null;
        try {
            response = callback.execute();
        } catch (IOException e) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "execute callback failed", e);

        }


        if (response.body() != null && response.isSuccessful()) {


            String bodyResponse = null;
            try {
                bodyResponse = response.body().string();

            } catch (IOException e) {
                throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "bodyResponse null", e);
            }
            // try {

            System.out.println("test  UploadThread pushdata start33333  " + partNum);
            if (AngelsGate.StringErroreHandler(bodyResponse)) {

                String data_response = null;
                try {
                    data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, ctx);
                } catch (GeneralSecurityException e) {
                    throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Decode Error", e);
                }

                System.out.println("test  UploadThread pushdata start44444  " + partNum);


                if (AngelsGate.ErroreHandler(data_response)) {

                    if (LocalErrorHandler(data_response)) {


                        System.out.println("test push data response  " + data_response);



                        switch (data_response) {
                            case "NOTICE_FILE_UPLOADDONE":


                                /////////////
                                System.out.println("test succ111 upload thread " + partNum);
                                uploadThreadInfo.setProgress(uploadThreadInfo.getPartSize());
                                uploadThreadInfo.setUploadThreadStatus(UploadThreadStatus.COMPLETED.getType());
                                uploadProgressListener.onProgressUploadThread();
                                uploadProgressListener.onUploadSuccessUploadThread();
                                System.out.println("test succ222 upload thread " + partNum);


                                break;

                            case "NOTICE_FILE_COMPLETE":
                                break;

                            case "NOTICE_FILE_ABORT":
                                break;

                            case "NOTICE_FILE_EXPIRE":
                                break;


                            case "ERROR_FILE_NOTFOUND":
                                break;


                        }


                    } else {
                        throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Data Incorect");
                    }


                } else {
                    throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Error in data sended");
                }

            } else {
                throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Error in first data sended ");

            }


        } else {
            System.out.println("test threadError response.code()" + response.code());

            System.out.println("test threadError response.errorBody()" + response.errorBody());


            throw new UploadException(UploadException.EXCEPTION_CONNECTION_ERROR, "Error Connect to Server");
        }


    }

    public static boolean LocalErrorHandler(String respose) {


        switch (respose) {
            case "ERROR_FILE_NOTFOUND":
                return false;

            case "ERROR_FILE_OWNERNOTMATCH":
                return false;


            case "ERROR_FILE_WRONGPARTNUM":
                return false;


            case "ERROR_FILE_NEEDREUPLOAD":
                return false;


            case "ERROR_FILE_PARTUPLOADED":
                return false;


            case "ERROR_FILE_CHECKSUMNOTMATCH":
                return false;


            case "ERROR_FILE_CANNOTWRITE":
                return false;


            case "ERROR_FILE_BADENCODING":
                return false;


            case "ERROR_FILE_BADCOMPRESSION":
                return false;

            case "NOTICE_FILE_UPLOADFAIL":
                return false;

            case "ERROR_FILE_UNKNOWNINFO":
                return false;


            default:
                return true;
        }
    }

    public UploadThreadInfoModel getUploadThreadInfo() {
        return uploadThreadInfo;
    }


}
