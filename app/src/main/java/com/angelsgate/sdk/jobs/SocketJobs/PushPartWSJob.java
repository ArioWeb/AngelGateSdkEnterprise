package com.angelsgate.sdk.jobs.SocketJobs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.FileUploadSessionPartRequest;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;
import com.angelsgate.sdk.AngelsGateUtils.NetworkUtils;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobParameters;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.PushPartEvent;
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

public class PushPartWSJob extends ContextJob {
    private CountDownLatch completeSignal = new CountDownLatch(1);

    private static final String TAG = PushPartWSJob.class.getSimpleName();
    private static final long serialVersionUID = 2L;
    private static final int MAX_ATTACHMENT_SIZE_BYTE = 1 * 1024 * 1024 * 1024;


    private static final String KEY_handler = "handler";
    private static final String KEY_part = "partNumber";


    private static final String KEY_deviceId = "file_deviceId";
    public static final String KEY_RESULT = "result";
    public static final String KEY_CANCEL_MESSAGE = "cancel";
    public static final String KEY_RESPONSE = "response";
    String bodyResponse = null;
    boolean statusREesponse = false;
    ///////////////////////////////

    Context ctx;


    public PushPartWSJob(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        ctx = context;
        EventBus.getDefault().register(this);

    }


    public static JobParameters constructParameters() {

        JobParameters.Builder builder = JobParameters.newBuilder();
        builder.withRetryCount(5);
        builder.withNetworkRequirement();
        return builder.create();
    }


    public static Data.Builder constructData(String handler, int partNumber, String deviceId) {
        Data.Builder dataBuilder = new Data.Builder()
                .putString(PushPartWSJob.KEY_handler, handler)
                .putInt(PushPartWSJob.KEY_part, partNumber)
                .putString(PushPartWSJob.KEY_deviceId, deviceId);

        return dataBuilder;
    }


    @Override
    public Result onRun() throws IOException {


        try {


            Data data = getInputData();
            final String handler = data.getString(KEY_handler);
            int partNumber = data.getInt(KEY_part, -1);


            AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());

            String part_data = databaseHelper.loadUploadPart(handler, partNumber).getData();
            String checksum = EncodeAlgorithmUtils.md5(part_data);



            String deviceId = data.getString(KEY_deviceId);


            ////RequestHeader
            final int segment = AngelsGate.CreatSegment(getContext());
            final String Ssalt = AngelsGate.CreatSsalt();
            final long TimeStamp = AngelsGate.CreatTimeStamp();
            final String Request = "PushPart";
            boolean isArrayRequest = false;
            final String DeviceId = deviceId;




            FileUploadSessionPartRequest input = new FileUploadSessionPartRequest(handler, partNumber + 1, part_data, checksum);


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
            final String rawJson = NetworkUtils.bodyToString(requestBody);




            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

            final Handler handlerobject = new Handler(Looper.getMainLooper());
            handlerobject.postDelayed(new Runnable() {
                @Override
                public void run() {

                    WebSocketActivity.sendMessage(rawJson);

            }
        } ,300);

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


                                databaseHelper.deleteUploadPart(handler, partNumber);


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


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCreatSessionEvent(PushPartEvent event) {

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
