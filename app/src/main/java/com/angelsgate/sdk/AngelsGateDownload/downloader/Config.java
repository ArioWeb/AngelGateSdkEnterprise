package com.angelsgate.sdk.AngelsGateDownload.downloader;

import android.content.Context;


import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.ApiInterface;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Config {


    private int readTimeout;
    private int connectTimeout;
    private String userAgent;
    private OkHttpClient httpClient;
    private ApiInterface apiInterface;

    private int downloadTasks;
    private int downloadThread;
    private int retryDownloadThreadCount;

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


    public int getDownloadTasks() {
        return downloadTasks;
    }

    public void setDownloadTasks(int downloadTasks) {
        this.downloadTasks = downloadTasks;
    }

    public int getDownloadThread() {
        return downloadThread;
    }

    public void setDownloadThread(int downloadThread) {
        this.downloadThread = downloadThread;
    }

    public int getRetryDownloadThreadCount() {
        return retryDownloadThreadCount;
    }

    public void setRetryDownloadThreadCount(int retryDownloadThreadCount) {
        this.retryDownloadThreadCount = retryDownloadThreadCount;
    }


    private Config(Builder builder) {

        this.readTimeout = builder.readTimeout;
        this.connectTimeout = builder.connectTimeout;
        this.userAgent = builder.userAgent;
        this.httpClient = builder.httpClient;
        this.apiInterface = builder.apiInterface;
        this.downloadTasks = builder.downloadTasks;
        this.downloadThread = builder.downloadThread;
        this.retryDownloadThreadCount = builder.retryDownloadThreadCount;

    }

    public static Builder newBuilder(Context ctx) {
        return new Builder(ctx);
    }

    public static class Builder {

        Context ctx;
        int readTimeout = Constants.DEFAULT_READ_TIMEOUT_IN_MILLS;
        int connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT_IN_MILLS;
        String userAgent = Constants.DEFAULT_USER_AGENT;
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new EncodeRequestInterceptor(ctx))
                .build();

        ApiInterface apiInterface = getRetrofit(httpClient);

        private int downloadTasks = Constants.downloadTasks;
        private int downloadThread = Constants.downloadThread;
        private int retryDownloadThreadCount = Constants.retryDownloadThreadCount;


        public Builder(Context ctx) {
            this.ctx = ctx;
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


        public Builder sedownloadTasks(int downloadTasks) {
            this.downloadTasks = downloadTasks;
            return this;
        }

        public Builder setdownloadThread(int downloadThread) {
            this.downloadThread = downloadThread;
            return this;
        }


        public Builder setretryDownloadThreadCount(int retryDownloadThreadCount) {
            this.retryDownloadThreadCount = retryDownloadThreadCount;
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
