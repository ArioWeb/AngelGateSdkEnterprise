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

package com.angelsgate.sdk.AngelsGateDownload.downloader.network.request;


import com.angelsgate.sdk.AngelsGateDownload.downloader.Priority;



public class DownloadRequestBuilder implements RequestBuilder {

    String handler;
    String deviceId;
    String dirPath;
    String fileName;
    Priority priority = Priority.MEDIUM;
    int readTimeout;
    int connectTimeout;
    String userAgent="";


    public DownloadRequestBuilder(String handler, String deviceId, String dirPath, String fileName) {
        this.handler = handler;
        this.deviceId = deviceId;
        this.dirPath = dirPath;
        this.fileName = fileName;
    }



    @Override
    public DownloadRequestBuilder setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }



    @Override
    public DownloadRequestBuilder setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public DownloadRequestBuilder setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public DownloadRequestBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public DownloadRequest build() {
        return new DownloadRequest(this);
    }

}
