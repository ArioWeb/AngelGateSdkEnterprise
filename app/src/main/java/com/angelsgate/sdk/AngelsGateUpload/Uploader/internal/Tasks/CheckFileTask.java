package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.Tasks;

import android.content.Context;
import android.os.Process;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.HandlerObject;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException.EXCEPTION_OTHER;

public class CheckFileTask implements Runnable {


    private UploadRequest request;
    private CheckFileListener checkFileListener;
    Context ctx;
    private int retryUploadCount;

    public CheckFileTask(UploadRequest request, CheckFileListener checkFileListener, Context ctx) {
        this.request = request;
        this.checkFileListener = checkFileListener;
        this.ctx = ctx;
        this.retryUploadCount = 0;
    }


    @Override
    public void run() {

        System.out.println("test CreateSessionTask  run");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            CheckFile();
        } catch (UploadException e) {


            if (retryUploadCount >= ComponentHolder.getInstance().getRetryUploadThreadCount()) {
                retryUploadCount = 0;
                checkFileListener.onFailedcheckFile(e);
                return;

            }

            retryUploadCount++;
            run();


        } catch (Exception e) {
            if (retryUploadCount >= ComponentHolder.getInstance().getRetryUploadThreadCount()) {
                retryUploadCount = 0;
                checkFileListener.onFailedcheckFile(new UploadException(EXCEPTION_OTHER, e));
                return;

            }
            retryUploadCount++;
            run();


        }

    }


    public interface CheckFileListener {

        void onSuccesscheckFile(String response);

        void onFailedcheckFile(UploadException exception);
    }


    public void CheckFile() {


        try {


            ////RequestHeader
            final int segment = AngelsGate.CreatSegment(ctx);
            final String Ssalt = AngelsGate.CreatSsalt();
            final long TimeStamp = AngelsGate.CreatTimeStamp();
            final String Request = "CheckFile";
            boolean isArrayRequest = false;
            final String DeviceId = request.getDeviceId();


            HandlerObject handlerObject = new HandlerObject(request.getHandler());

            Call<ResponseBody> callback = ComponentHolder.getInstance().getApiInterface().CheckFile(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, handlerObject);
            Response<ResponseBody> response = null;
            try {
                response = callback.execute();
            } catch (IOException e) {
                throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "execute callback failed", e);
            }

            if (response.body() != null && response.isSuccessful()) {


                String bodyResponse = null;
                try {
                    bodyResponse = response.body().string();

                } catch (IOException e) {
                    throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "bodyResponse null", e);
                }
                try {

                    System.out.println("test CheckFileTask bodyResponse" + bodyResponse);

                    if (AngelsGate.StringErroreHandler(bodyResponse)) {

                        String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, ctx);

                        System.out.println("test CheckFileTask data_response" + data_response);

                        if (AngelsGate.ErroreHandler(data_response)) {


                            if (LocalErrorHandler(data_response)) {


                                checkFileListener.onSuccesscheckFile(data_response);


                            } else {
                                throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Data Incorect");
                            }


                        } else {
                            throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Error in data sended");
                        }

                    } else {
                        throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Error in first data sended ");

                    }


                } catch (GeneralSecurityException e) {
                    throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Decode Error", e);
                }


            } else {
                throw new UploadException(UploadException.EXCEPTION_CONNECTION_ERROR, "Error Connect to Server");
            }


        } catch (HttpException e) {

            throw new UploadException(UploadException.EXCEPTION_CONNECTION_ERROR, "HttpException", e);
        }

    }


    public static boolean LocalErrorHandler(String respose) {


        switch (respose) {
            case "ERROR_FILE_NOTFOUND":
                return false;

            case "ERROR_FILE_OWNERNOTMATCH":
                return false;


            default:
                return true;
        }
    }
}
