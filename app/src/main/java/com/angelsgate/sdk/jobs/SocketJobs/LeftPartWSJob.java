package com.angelsgate.sdk.jobs.SocketJobs;

import android.content.Context;

import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.HandlerObject;
import com.angelsgate.sdk.AngelsGateUtils.NetworkUtils;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobParameters;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.LeftPartEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.WebSocketActivity;
import com.angelsgate.sdk.jobs.ContextJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CountDownLatch;

import okhttp3.RequestBody;
import retrofit2.HttpException;

public class LeftPartWSJob extends ContextJob {

    private CountDownLatch completeSignal = new CountDownLatch(1);

    private static final String TAG = LeftPartWSJob.class.getSimpleName();
    private static final long serialVersionUID = 2L;
    private static final int MAX_ATTACHMENT_SIZE_BYTE = 1 * 1024 * 1024 * 1024;


    private static final String KEY_handler = "handler";


    private static final String KEY_deviceId = "file_deviceId";
    public static final String KEY_RESULT = "result";
    public static final String KEY_CANCEL_MESSAGE = "cancel";
    public static final String KEY_RESPONSE = "response";


    String bodyResponse = null;
    boolean statusREesponse = false;
    ///////////////////////////////

    public LeftPartWSJob(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        EventBus.getDefault().register(this);
    }


    public static JobParameters constructParameters() {

        JobParameters.Builder builder = JobParameters.newBuilder();
        builder.withRetryCount(3);
        builder.withNetworkRequirement();
        return builder.create();
    }


    public static Data.Builder constructData(String handler, String deviceId) {
        Data.Builder dataBuilder = new Data.Builder()
                .putString(LeftPartWSJob.KEY_handler, handler)
                .putString(LeftPartWSJob.KEY_deviceId, deviceId);

        return dataBuilder;
    }


    @Override
    public Result onRun() throws IOException {

        try {


            Data data = getInputData();
            String handler = data.getString(KEY_handler);
            String deviceId = data.getString(KEY_deviceId);


            ////RequestHeader
            final int segment = AngelsGate.CreatSegment(getContext());
            final String Ssalt = AngelsGate.CreatSsalt();
            final long TimeStamp = AngelsGate.CreatTimeStamp();
            final String Request = "LeftPart";
            boolean isArrayRequest = false;
            final String DeviceId = deviceId;

            HandlerObject handlerObject = new HandlerObject(handler);


            String inputString = NetworkUtils.ConvertObjectToString(handlerObject);
            okhttp3.Request request = NetworkUtils.CreateRequestForSocket(inputString);

            try {
                request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
            databaseHelper.insertSocketRequest(new SocketRequest(segment, Ssalt, Request));

            RequestBody requestBody = request.body();
            String rawJson = NetworkUtils.bodyToString(requestBody);
            WebSocketActivity.sendMessage(rawJson);

            try {
                completeSignal.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (statusREesponse) {




                    if (AngelsGate.StringErroreHandler(bodyResponse)) {

                        String data_response = bodyResponse;




                        if (AngelsGate.ErroreHandler(data_response)) {

                            if (LocalErrorHandler(data_response)) {
                                Data output = new Data.Builder()
                                        .putString(KEY_RESULT, "success")
                                        .putString(KEY_RESPONSE, data_response)
                                        .build();

                                return Result.success(output);


                            } else {
                                Data ErrorData1 = ApiError("خطای عمومی");
                                return Result.failure(ErrorData1);
                            }


                        } else {
                            Data ErrorData1 = ApiError("خطای عمومی");
                            return Result.failure(ErrorData1);
                        }

                    } else {
                        Data ErrorData1 = ApiError("خطای عمومی");
                        return Result.failure(ErrorData1);

                    }




            }else{
                Data ErrorData1 = ApiError("خطا");
                return Result.failure(ErrorData1);

            }



        } catch (HttpException e) {

            throw e;
        }


    }


    public static Data ApiError(String ErrorMessage) {
        Data output = new Data.Builder()
                .putString(KEY_RESULT, "Cancel")
                .putString(KEY_CANCEL_MESSAGE, ErrorMessage)
                .build();
        return output;

    }


    @Override
    public Data onCanceled() {

        Data output = new Data.Builder()
                .putString(KEY_RESULT, "Cancel")
                .putString(KEY_CANCEL_MESSAGE, "خطا در اتصال به سرور")
                .build();

        return output;
    }


    @Override
    protected boolean onShouldRetry(Exception exception) {
        return (exception instanceof HttpException);

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


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCreatSessionEvent(LeftPartEvent event) {

        statusREesponse = event.isStatus();

        if (statusREesponse) {
            bodyResponse = event.getResponseBody();
        }


        completeSignal.countDown();
    }


    @Override
    public void onStopped() {
        super.onStopped();
        EventBus.getDefault().unregister(this);
    }

}
