package com.angelsgate.sdk.AngelsGateDownload.downloader.internal;

import android.content.Context;
import android.os.Process;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadRequestStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadPauseException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;

import com.angelsgate.sdk.AngelsGateNetwork.model.file.download.GetFileInfoResponse;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.download.HandlerObject;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException.EXCEPTION_OTHER;


public class GetFileInfoTask implements Runnable {


    private final DownloadRequest request;
    private final OnGetFileInfoListener onGetFileInfoListener;

    Context ctx;

    public GetFileInfoTask(DownloadRequest request,
                           OnGetFileInfoListener onGetFileInfoListener, Context ctx) {
        this.request = request;
        this.ctx = ctx;
        this.onGetFileInfoListener = onGetFileInfoListener;
    }


    @Override
    public void run() {

        System.out.println("test GetFileInfoTask  run" );
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            GetFileInfo(request.getHandler(), request.getDeviceId());
        } catch (DownloadException e) {
            onGetFileInfoListener.onFailedGetFileInfo(e);
        } catch (Exception e) {
            onGetFileInfoListener.onFailedGetFileInfo(new DownloadException(EXCEPTION_OTHER, e));
        }

    }


    private void checkPause() {
        if (request.getStatus() == DownloadRequestStatus.PAUSED.getType()) {
            throw new DownloadPauseException(DownloadException.EXCEPTION_PAUSE);
        }
    }

    /**
     * Get file info listener.
     */
    public interface OnGetFileInfoListener {

        void onSuccessGetFileInfo(GetFileInfoResponse response, boolean isSupportRanges);

        void onFailedGetFileInfo(DownloadException exception);
    }


    public void GetFileInfo(final String handler, String deviceId) {

        System.out.println("test GetFileInfoTask  GetFileInfo request" );


        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(ctx);
        final String Ssalt = AngelsGate.CreatSsalt();

        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "GetFileInfo";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;

        System.out.println("test DeviceId "+DeviceId);

        HandlerObject handlerObject = new HandlerObject(handler);

        Call<ResponseBody> callback = ComponentHolder.getInstance().getApiInterface().GetFileInfo(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, handlerObject);
        Response<ResponseBody> response = null;
        try {
            response = callback.execute();
        } catch (IOException e) {
            throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "execute callback failed", e);
        }

        if (response.body() != null && response.isSuccessful()) {


            String bodyResponse = null;
            try {
                bodyResponse = response.body().string();

            } catch (IOException e) {
                throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "bodyResponse null", e);
            }




            if (AngelsGate.StringErroreHandler(bodyResponse)) {

                String data_response = null;
                try {
                    data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, ctx);
                } catch (GeneralSecurityException e) {
                    throw new DownloadException(DownloadException.EXCEPTION_PROTOCOL, "Decode Error", e);
                }

                System.out.println("test GetFileInfoTask  GetFileInfo data_response "+data_response );


                if (AngelsGate.ErroreHandler(data_response)) {


                    if (LocalErrorHandler(data_response)) {


                        Gson convertor = new Gson();
                        GetFileInfoResponse ApiResponseGetFileInfoObject = convertor.fromJson(data_response, GetFileInfoResponse.class);

                        checkPause();
                        onGetFileInfoListener.onSuccessGetFileInfo(ApiResponseGetFileInfoObject, true);

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
            throw new DownloadException(DownloadException.EXCEPTION_CONNECTION_ERROR, "Error Connect to Server");
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
