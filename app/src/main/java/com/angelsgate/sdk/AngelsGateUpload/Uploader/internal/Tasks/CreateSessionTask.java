package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.Tasks;

import android.content.Context;
import android.os.Process;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.SessionResponse;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.UploadSessionRequest;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CreateSessionTask implements Runnable {

    private UploadRequest request;
    private CreateSessionListener createSessionListener;

    Context ctx;
    private int retryUploadCount;

    public CreateSessionTask(UploadRequest request,
                             CreateSessionListener createSessionListener, Context ctx) {
        this.request = request;
        this.ctx = ctx;
        this.createSessionListener = createSessionListener;
        this.retryUploadCount = 0;
    }


    @Override
    public void run() {

        System.out.println("test CreateSessionTask  run");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        //   try {
        try {
            CreateSession();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (UploadException e) {
//
//            if (retryUploadCount >= ComponentHolder.getInstance().getRetryUploadThreadCount()) {
//                retryUploadCount = 0;
//                createSessionListener.onFailedcreateSession(e);
//                return;
//
//            }
//
//            retryUploadCount++;
//            run();
//
//
//
//        } catch (Exception e) {
//
//            if (retryUploadCount >= ComponentHolder.getInstance().getRetryUploadThreadCount()) {
//                retryUploadCount = 0;
//                createSessionListener.onFailedcreateSession(new UploadException(EXCEPTION_OTHER, e));
//                return;
//
//            }
//
//            retryUploadCount++;
//            run();
//
//        }

    }

    /**
     * CreateSession listener.
     */
    public interface CreateSessionListener {

        void onSuccesscreateSession(SessionResponse response);

        void onFailedcreateSession(UploadException exception);
    }


    public void CreateSession() throws IOException {


        // try {

        System.out.println("test CreateSession");

        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(ctx);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "StoreFile";
        boolean isArrayRequest = false;
        final String DeviceId = request.getDeviceId();


        System.out.println("test CreateSession 2222222");


        UploadSessionRequest input = new UploadSessionRequest(request.getFilename(), request.getFilesize(), request.getExtention(), request.getChecksum(), request.getThumb());

        System.out.println("test CreateSession 33333");
        Call<ResponseBody> callback = ComponentHolder.getInstance().getApiInterface().CreateSession(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, input);
        Response<ResponseBody> response = null;

        System.out.println("test CreateSession 4444444");

      //  try {

            System.out.println("test CreateSession 555555");
            response = callback.execute();

            System.out.println("test CreateSession 666666");
//        } catch (IOException e) {
////                throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "execute callback failed", e);
//
//            System.out.println("test CreateSession 7777777");
//            throw new RuntimeException("execute callback failed");
//
//        }


        System.out.println("test CreateSession 8888888");

        if (response.body() != null && response.isSuccessful()) {


            String bodyResponse = null;
            try {
                bodyResponse = response.body().string();

            } catch (IOException e) {
                //throw new UploadException(UploadException.EXCEPTION_IO_EXCEPTION, "bodyResponse null", e);

                throw new RuntimeException("bodyResponse null");
            }




            try {


                System.out.println("test createUploadSession bodyResponse" + bodyResponse);


                if (AngelsGate.StringErroreHandler(bodyResponse)) {

                    String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, ctx);


                    System.out.println("test createUploadSession data_response" + data_response);

                    if (AngelsGate.ErroreHandler(data_response)) {


                        if (data_response.equals("ERROR_PROCESS_REFUSED") || data_response.equals("ERROR_FILE_USERNOTEXIST") || data_response.equals("ERROR_FILE_OVERFLOW")) {

                           // throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Data Incorect");

                            throw new RuntimeException("Data Incorect");

                        } else {

                            Gson convertor = new Gson();
                            SessionResponse itemsObject = convertor.fromJson(data_response, SessionResponse.class);
                            createSessionListener.onSuccesscreateSession(itemsObject);


                        }


                    } else {
                        //throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Error in data sended");

                        throw new RuntimeException("Error in data sended");
                    }

                } else {
                   // throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Error in first data sended ");

                    throw new RuntimeException("Error in first data sended");

                }


            } catch (GeneralSecurityException e) {
                //throw new UploadException(UploadException.EXCEPTION_PROTOCOL, "Decode Error", e);

                throw new RuntimeException("Decode Error");
            }


        } else {
            //throw new UploadException(UploadException.EXCEPTION_CONNECTION_ERROR, "Error Connect to Server");

            throw new RuntimeException("rror Connect to Server");
        }


//        } catch (HttpException e) {
//
//            throw new UploadException(UploadException.EXCEPTION_CONNECTION_ERROR, "HttpException", e);
//        }


    }


}
