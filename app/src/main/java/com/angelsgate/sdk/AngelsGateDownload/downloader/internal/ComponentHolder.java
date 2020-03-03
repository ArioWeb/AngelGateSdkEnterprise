/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.angelsgate.sdk.AngelsGateDownload.downloader.internal;

import android.content.Context;

import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.dao.DbHelper;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Config;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Constants;
import com.angelsgate.sdk.AngelsGateDownload.downloader.OMDownloader;
import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.ApiInterface;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ComponentHolder {

    private static ComponentHolder INSTANCE;
    private int readTimeout;
    private int connectTimeout;
    private String userAgent;
    private OkHttpClient httpClient;
    private DbHelper dbHelper;
    private Context context;
    private ApiInterface apiInterface;
    private int downloadTasks;
    private int downloadThread;
    private int retryDownloadThreadCount;


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
        this.dbHelper = new AppDbHelper(context);
        this.downloadTasks = config.getDownloadTasks();
        this.downloadThread = config.getDownloadThread();
        this.retryDownloadThreadCount = config.getRetryDownloadThreadCount();
        this.context = context;
        OMDownloader.cleanUp(30);
    }


    public    int getDownloadTasks() {
        if (downloadTasks == 0) {
            synchronized (ComponentHolder.class) {
                if (downloadTasks == 0) {
                    downloadTasks = Constants.downloadTasks;
                }
            }
        }
        return downloadTasks;
    }


    public int getDownloadThread() {
        if (downloadThread == 0) {
            synchronized (ComponentHolder.class) {
                if (downloadThread == 0) {
                    downloadThread = Constants.downloadThread;
                }
            }
        }
        return downloadThread;
    }


    public int getRetryDownloadThreadCount() {
        if (retryDownloadThreadCount == 0) {
            synchronized (ComponentHolder.class) {
                if (retryDownloadThreadCount == 0) {
                    retryDownloadThreadCount = Constants.retryDownloadThreadCount;
                }
            }
        }
        return retryDownloadThreadCount;
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

    public   DbHelper getDbHelper() {
        if (dbHelper == null) {
            synchronized (ComponentHolder.class) {
                if (dbHelper == null) {
                    dbHelper = new AppDbHelper(context);
                }
            }
        }
        return dbHelper;
    }

    public   OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (ComponentHolder.class) {
                if (httpClient == null) {
                    httpClient = new OkHttpClient.Builder()
                            .addInterceptor(new EncodeRequestInterceptor(context.getApplicationContext()))
                            .build();


                }
            }
        }
        return httpClient;
    }


    public   ApiInterface getApiInterface() {
        if (apiInterface == null) {
            synchronized (ComponentHolder.class) {
                if (apiInterface == null) {
                    apiInterface = getRetrofit(getHttpClient());
                }
            }
        }
        return apiInterface;
    }


    public   ApiInterface getRetrofit(OkHttpClient okHttpClient) {

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
