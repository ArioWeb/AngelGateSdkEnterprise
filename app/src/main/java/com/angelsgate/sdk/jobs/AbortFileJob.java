package com.angelsgate.sdk.jobs;

import android.content.Context;

import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.HandlerObject;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobParameters;
import com.angelsgate.sdk.ApiInterface;
import com.angelsgate.sdk.MainActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AbortFileJob extends ContextJob {

    private static final String TAG = AbortFileJob.class.getSimpleName();
    private static final long serialVersionUID = 2L;
    private static final int MAX_ATTACHMENT_SIZE_BYTE = 1 * 1024 * 1024 * 1024;


    private static final String KEY_handler = "handler";
    private static final String KEY_deviceId = "file_deviceId";
    public static final String KEY_RESULT = "result";
    public static final String KEY_CANCEL_MESSAGE = "cancel";
    public static final String KEY_RESPONSE = "response";


    public AbortFileJob(Context context, WorkerParameters workerParams ) {
        super(context, workerParams );
    }


    public static JobParameters constructParameters() {

        JobParameters.Builder builder = JobParameters.newBuilder();
        builder.withRetryCount(5);
        builder.withNetworkRequirement();
        return builder.create();
    }


    public static Data.Builder constructData(String handler, String deviceId) {
        Data.Builder dataBuilder = new Data.Builder()
                .putString(AbortFileJob.KEY_handler, handler)
                .putString(AbortFileJob.KEY_deviceId, deviceId);
        return dataBuilder;
    }


    @Override
    public Result onRun() throws IOException {
        Data data = getInputData();
        String handler = data.getString(KEY_handler);
        String deviceId = data.getString(KEY_deviceId);


        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(getContext());
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "AbortFile";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;

        HandlerObject handlerObject = new HandlerObject(handler);

        Call<ResponseBody> callback = MainActivity.apiInterface2.AbortFile(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, handlerObject);
        Response<ResponseBody> response = callback.execute();

        if (response.body() != null && response.isSuccessful()) {


            String bodyResponse = null;
            try {
                bodyResponse = response.body().string();

            } catch (IOException e) {
                Data ErrorData1 = ApiError("خطا در دریافت پاسخ از سرور");
                return Result.failure(ErrorData1);
            }
            try {

                if (AngelsGate.StringErroreHandler(bodyResponse)) {

                    String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, getContext());



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


            } catch (GeneralSecurityException e) {
                Data ErrorData1 = ApiError("خطا");
                return Result.failure(ErrorData1);
            }


        } else {
            HttpException t = new HttpException(response);

            throw t;
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


            default:
                return true;
        }
    }
}
