

package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal;

import android.content.Context;


import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Config;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Constants;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.OMUploader;
import com.angelsgate.sdk.ApiInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ComponentHolder {

    private static ComponentHolder INSTANCE;
    private int readTimeout;
    private int connectTimeout;
    private String userAgent;
    private OkHttpClient httpClient;
    private Context context;
    private ApiInterface apiInterface;
    private int uploadTasks;
    private int uploadThread;
    private int retryUploadThreadCount;


    public static ComponentHolder getInstance() {
        synchronized (ComponentHolder.class) {
            if (INSTANCE == null) {
                INSTANCE = new ComponentHolder();
            }
        }
        return INSTANCE;
    }

    public void init(Context context, Config config) {
        this.readTimeout = config.getReadTimeout();
        this.connectTimeout = config.getConnectTimeout();
        this.userAgent = config.getUserAgent();
        this.httpClient = config.getHttpClient();
        this.apiInterface = config.getApiInterface();
        this.uploadThread = config.getUploadThread();
        this.retryUploadThreadCount = config.getRetryuploadThreadCount();
        this.context = context;
        OMUploader.cleanUp(30);
    }


    public int getUploadTasks() {
        if (uploadTasks == 0) {
            synchronized (ComponentHolder.class) {
                if (uploadTasks == 0) {
                    uploadTasks = Constants.uploadTasks;
                }
            }
        }
        return uploadTasks;
    }


    public int getUploadThread() {
        if (uploadThread == 0) {
            synchronized (ComponentHolder.class) {
                if (uploadThread == 0) {
                    uploadThread = Constants.uploadThread;
                }
            }
        }
        return uploadThread;
    }


    public int getRetryUploadThreadCount() {
        if (retryUploadThreadCount == 0) {
            synchronized (ComponentHolder.class) {
                if (retryUploadThreadCount == 0) {
                    retryUploadThreadCount = Constants.retryuploadThreadCount;
                }
            }
        }
        return retryUploadThreadCount;
    }


    public int getReadTimeout() {
        if (readTimeout == 0) {
            synchronized (ComponentHolder.class) {
                if (readTimeout == 0) {
                    readTimeout = Constants.DEFAULT_READ_TIMEOUT_IN_MILLS;
                }
            }
        }
        return readTimeout;
    }

    public int getConnectTimeout() {
        if (connectTimeout == 0) {
            synchronized (ComponentHolder.class) {
                if (connectTimeout == 0) {
                    connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT_IN_MILLS;
                }
            }
        }
        return connectTimeout;
    }

    public String getUserAgent() {
        if (userAgent == null) {
            synchronized (ComponentHolder.class) {
                if (userAgent == null) {
                    userAgent = Constants.DEFAULT_USER_AGENT;
                }
            }
        }
        return userAgent;
    }


    public OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (ComponentHolder.class) {
                if (httpClient == null) {
                    httpClient = new OkHttpClient.Builder()
                            .addInterceptor(new EncodeRequestInterceptor(context.getApplicationContext()))
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .build();


                }
            }
        }
        return httpClient;
    }


    public ApiInterface getApiInterface() {
        if (apiInterface == null) {
            synchronized (ComponentHolder.class) {
                if (apiInterface == null) {
                    apiInterface = getRetrofit(getHttpClient());
                }
            }
        }
        return apiInterface;
    }


    public ApiInterface getRetrofit(OkHttpClient okHttpClient) {

        String baseUrl = "https://arioweb.com/api";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        return apiInterface;
    }

}
