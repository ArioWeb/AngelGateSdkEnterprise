

package com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request;


import com.angelsgate.sdk.AngelsGateUpload.Uploader.Priority;

import java.io.InputStream;


public class UploadRequestBuilder implements RequestBuilder {


    Priority priority = Priority.MEDIUM;
    int readTimeout;
    int connectTimeout;
    String userAgent = "";



    String filename;
    long filesize;
    String extention;
    String checksum;
    String thumb;
    String deviceId;
    String selectedPath;


    public UploadRequestBuilder(String filename, long filesize, String extention, String checksum, String thumb, String deviceId, String selectedPath) {

        this.filename = filename;
        this.filesize = filesize;
        this.extention = extention;
        this.checksum = checksum;
        this.thumb = thumb;
        this.deviceId = deviceId;
        this.selectedPath = selectedPath;
    }

    @Override
    public UploadRequestBuilder setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }


    @Override
    public UploadRequestBuilder setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public UploadRequestBuilder setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public UploadRequestBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public UploadRequest build() {
        return new UploadRequest(this);
    }

}
