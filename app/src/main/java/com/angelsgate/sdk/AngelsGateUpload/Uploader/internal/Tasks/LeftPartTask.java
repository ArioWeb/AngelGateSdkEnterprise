package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.Tasks;

import android.content.Context;
import android.os.Process;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.HandlerObject;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.LeftPartResponse;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.SessionResponse;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException.EXCEPTION_OTHER;

public class LeftPartTask implements Runnable {

    private UploadRequest request;
    private LeftPartListener leftPartListener;
    Context ctx;

    public LeftPartTask(UploadRequest request, LeftPartListener leftPartListener, Context ctx) {
        this.request = request;
        this.leftPartListener = leftPartListener;
        this.ctx = ctx;
    }


    @Override
    public void run() {

        System.out.println("test CreateSessionTask  run");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            LeftPart();
        } catch (UploadException e) {
            leftPartListener.onFailedleftPart(e);
        } catch (Exception e) {
            leftPartListener.onFailedleftPart(new UploadException(EXCEPTION_OTHER, e));
        }

    }


    public interface LeftPartListener {

        void onSuccessleftPart(SessionResponse response);

        void onFailedleftPart(UploadException exception);
    }



    public void LeftPart() {

        try {

            ////RequestHeader
            final int segment = AngelsGate.CreatSegment(ctx);
            final String Ssalt = AngelsGate.CreatSsalt();
            final long TimeStamp = AngelsGate.CreatTimeStamp();
            final String Request = "LeftPart";
            boolean isArrayRequest = false;
            final String DeviceId = request.getDeviceId();

            HandlerObject handlerObject = new HandlerObject(request.getHandler());

            Call<ResponseBody> callback = ComponentHolder.getInstance().getApiInterface().LeftParts(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, handlerObject);
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



                    if (AngelsGate.StringErroreHandler(bodyResponse)) {

                        String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request,ctx);




                        if (AngelsGate.ErroreHandler(data_response)) {

                            if (LocalErrorHandler(data_response)) {




                                if (data_response.equals("NOTICE_FILE_PARTSEMPTY")) {

                                } else {
                                    ArrayList<Integer> partsNotComplete = new ArrayList<>();
                                    String substring = data_response.substring(1, data_response.length() - 1);
                                    String[] partNumStrings = substring.split(",");

                                    for (int i = 0; i < partNumStrings.length; i++) {
                                        partsNotComplete.add(Integer.parseInt(partNumStrings[i]));

                                    }
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

            case "ERROR_FILE_UNKNOWNINFO":
                return false;

            default:
                return true;
        }
    }

}
