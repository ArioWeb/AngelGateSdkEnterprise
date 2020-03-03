package com.angelsgate.sdk.jobs.SocketJobs;

import android.content.Context;

import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.UploadSessionRequest;
import com.angelsgate.sdk.AngelsGateUtils.NetworkUtils;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobParameters;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.CreatSessionEvent;
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

public class CreateSessionWSJob extends ContextJob {

    private CountDownLatch completeSignal = new CountDownLatch(1);

    private static final String TAG = CreateSessionWSJob.class.getSimpleName();
    private static final long serialVersionUID = 2L;
    private static final int MAX_ATTACHMENT_SIZE_BYTE = 1 * 1024 * 1024 * 1024;
    private static final String KEY_realname = "realname";
    private static final String KEY_size = "file_size";
    private static final String KEY_extention = "file_extention";
    private static final String KEY_checksum = "file_checksum";
    private static final String KEY_thumb = "file_thumb";


    private static final String KEY_deviceId = "file_deviceId";
    public static final String KEY_RESULT = "result";
    public static final String KEY_CANCEL_MESSAGE = "cancel";
    public static final String KEY_RESPONSE = "response";
    String bodyResponse = null;
    boolean statusREesponse = false;
    ///////////////////////////////
    Context ctx;
////////////////////

    public CreateSessionWSJob(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        ctx = context;
        EventBus.getDefault().register(this);
    }


    public static JobParameters constructParameters() {

        JobParameters.Builder builder = JobParameters.newBuilder();
        builder.withRetryCount(3);
        builder.withNetworkRequirement();
        return builder.create();
    }


    public static Data.Builder constructData(String realname, long size, String extention, String checksumKey, String thumbKey, String deviceId) {
        Data.Builder dataBuilder = new Data.Builder()
                .putString(CreateSessionWSJob.KEY_realname, realname)
                .putLong(CreateSessionWSJob.KEY_size, size)
                .putString(CreateSessionWSJob.KEY_extention, extention)
                .putString(CreateSessionWSJob.KEY_checksum, checksumKey)
                .putString(CreateSessionWSJob.KEY_thumb, thumbKey)
                .putString(CreateSessionWSJob.KEY_deviceId, deviceId);
        return dataBuilder;
    }


    @Override
    public Result onRun() throws IOException {

        try {

            Data data = getInputData();
            String realname = data.getString(KEY_realname);
            long size = data.getLong(KEY_size, 0);
            String extention = data.getString(KEY_extention);
            String checksum = data.getString(KEY_checksum);


            AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
            String thumb = databaseHelper.loadUploadSessionBylocalId(realname + size).getThumb();

            ////////////////////////////////////////////////
            String deviceId = data.getString(KEY_deviceId);


            ////RequestHeader
            final int segment = AngelsGate.CreatSegment(getContext());
            final String Ssalt = AngelsGate.CreatSsalt();
            final long TimeStamp = AngelsGate.CreatTimeStamp();
            final String Request = "StoreFile";
            boolean isArrayRequest = false;
            final String DeviceId = deviceId;


            UploadSessionRequest input = new UploadSessionRequest(realname, size, extention, checksum, thumb);

            String inputString = NetworkUtils.ConvertObjectToString(input);
            okhttp3.Request request = NetworkUtils.CreateRequestForSocket(inputString);

            try {
                request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }



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


                            if (data_response.equals("ERROR_PROCESS_REFUSED") || data_response.equals("ERROR_FILE_USERNOTEXIST") || data_response.equals("ERROR_FILE_OVERFLOW")) {

                                Data ErrorData1 = ApiError("خطای عمومی");
                                return Result.failure(ErrorData1);

                            } else {
                                Data output = new Data.Builder()
                                        .putString(KEY_RESULT, "success")
                                        .putString(KEY_RESPONSE, data_response)
                                        .build();

                                databaseHelper.deleteUploadSession(realname + size);

                                return Result.success(output);

                            }


                        } else {
                            Data ErrorData1 = ApiError("خطای عمومی");
                            return Result.failure(ErrorData1);
                        }

                    } else {
                        Data ErrorData1 = ApiError("خطای عمومی");
                        return Result.failure(ErrorData1);

                    }


                } else {

                    Data ErrorData1 = ApiError("خطا");
                    return Result.failure(ErrorData1);

                }





        } catch (HttpException e) {
            throw e;
        }


    }


    public Data ApiError(String ErrorMessage) {
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


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCreatSessionEvent(CreatSessionEvent event) {

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
