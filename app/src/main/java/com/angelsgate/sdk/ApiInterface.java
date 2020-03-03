package com.angelsgate.sdk;


import com.angelsgate.sdk.AngelsGateNetwork.model.ExchangeTokenRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.LogDataRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.TestDataRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.FileUploadSessionPartRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.GetFilePartRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.HandlerObject;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.UploadSessionRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {


    @POST("App.php")
    Call<ResponseBody> PreAuth(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest);


    @POST("App.php")
    Call<ResponseBody> PostAuth(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest);

    @POST("App.php")
    Call<ResponseBody> Exchange(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body ExchangeTokenRequest input);


    @POST("App.php")
    Call<ResponseBody> TestApi(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body TestDataRequest input);


    @POST("Signal.php")
    Call<ResponseBody> signal(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body TestDataRequest input);


    @POST("Log.php")
    Call<ResponseBody> Log(@Body LogDataRequest input);


    /////////////////////file upload

    @POST("App.php")
    Call<ResponseBody> CreateSession(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body UploadSessionRequest input);

    @POST("App.php")
    Call<ResponseBody> PushPart(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body FileUploadSessionPartRequest data);

    @POST("App.php")
    Call<ResponseBody> AbortFile(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body HandlerObject handler);

    @POST("App.php")
    Call<ResponseBody> CheckFile(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body HandlerObject handler);

    @POST("App.php")
    Call<ResponseBody> LeftParts(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body HandlerObject handler);

    @POST("App.php")
    Call<ResponseBody> DeleteFile(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body HandlerObject handler);

    @POST("App.php")
    Call<ResponseBody> ForwardFile(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body HandlerObject handler, @Body String peer);





    /////////////////////file download

    @POST("App.php")
    Call<ResponseBody> GetFileInfo(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body  com.angelsgate.sdk.AngelsGateNetwork.model.file.download.HandlerObject handler);

    @POST("App.php")
    Call<ResponseBody> GetFilePart(@Header("Timestamp") long timestamp, @Header("DeviceId") String deviceId, @Header("Segment") long segment, @Header("Ssalt") String Ssalt, @Header("Request") String nameMethode, @Header("isArrayRequest") boolean isArrayRequest, @Body  com.angelsgate.sdk.AngelsGateNetwork.model.file.download.GetFilePartRequest input);


}
