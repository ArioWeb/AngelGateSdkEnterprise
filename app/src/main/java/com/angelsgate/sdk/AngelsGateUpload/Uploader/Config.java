package com.angelsgate.sdk.AngelsGateUpload.Uploader;

import android.content.Context;

import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.ApiInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Config {


    private int readTimeout;
    private int connectTimeout;
    private String userAgent;
    private OkHttpClient httpClient;
    private ApiInterface apiInterface;

    private int uploadTasks;
    private int uploadThread;
    private int retryuploadThreadCount;

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public int getUploadTasks() {
        return uploadTasks;
    }

    public void setUploadTasks(int uploadTasks) {
        this.uploadTasks = uploadTasks;
    }

    public int getUploadThread() {
        return uploadThread;
    }

    public void setUploadThread(int uploadThread) {
        this.uploadThread = uploadThread;
    }

    public int getRetryuploadThreadCount() {
        return retryuploadThreadCount;
    }

    public void setRetryuploadThreadCount(int retryuploadThreadCount) {
        this.retryuploadThreadCount = retryuploadThreadCount;
    }

    private Config(Builder builder) {

        this.readTimeout = builder.readTimeout;
        this.connectTimeout = builder.connectTimeout;
        this.userAgent = builder.userAgent;
        this.httpClient = builder.httpClient;
        this.apiInterface = builder.apiInterface;
        this.uploadTasks = builder.uploadTasks;
        this.uploadThread = builder.uploadThread;
        this.retryuploadThreadCount = builder.retryuploadThreadCount;

    }

    public static Builder newBuilder(Context ctx) {
        return new Builder(ctx);
    }

    public static class Builder {

        Context ctx;
        int readTimeout = Constants.DEFAULT_READ_TIMEOUT_IN_MILLS;
        int connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT_IN_MILLS;
        String userAgent = Constants.DEFAULT_USER_AGENT;
        OkHttpClient httpClient;
        ApiInterface apiInterface;

        private int uploadTasks = Constants.uploadTasks;
        private int uploadThread = Constants.uploadThread;
        private int retryuploadThreadCount = Constants.retryuploadThreadCount;


        public Builder(Context ctx) {
            this.ctx = ctx;

            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new EncodeRequestInterceptor(ctx.getApplicationContext()))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();


            apiInterface = getRetrofit(httpClient);
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder setHttpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }


        public Builder setApiInterface(ApiInterface apiInterface) {
            this.apiInterface = apiInterface;
            return this;
        }


        public Builder seuploadTasks(int uploadTasks) {
            this.uploadTasks = uploadTasks;
            return this;
        }

        public Builder setuploadThread(int uploadThread) {
            this.uploadThread = uploadThread;
            return this;
        }


        public Builder setretryuploadThreadCount(int retryuploadThreadCount) {
            this.retryuploadThreadCount = retryuploadThreadCount;
            return this;
        }


        public Config build() {
            return new Config(this);
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


}
