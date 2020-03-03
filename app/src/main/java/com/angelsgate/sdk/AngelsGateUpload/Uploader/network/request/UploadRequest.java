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

package com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request;


import android.content.Context;

import androidx.annotation.NonNull;

import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnCancelListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnProgressListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnStartListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnUploadFailedListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnUploadListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnWaitListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Priority;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.UploadRequestStatus;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Utils.Utils;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.core.UploadCore;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.UploadRequestQueue;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.UploadThreadInfoModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class UploadRequest {


    private String uploadId;
    private int status;
    private long progress;//////oooooooooooooo

    private List<UploadThreadInfoModel> uploadThreadInfos;/////////////oooooooooooooo
    ///////////////////////////////////////////////

    String filename;
    long filesize;
    String extention;
    String checksum;
    String thumb;
    String deviceId;
    String selectedPath;

    //////////////

    String handler;
    int partsize;
    int totalpart;

    /////////////////////////////////////////

    private Priority priority;

    private int readTimeout;

    private int connectTimeout;

    private String userAgent;

    private int sequenceNumber;

    private OnProgressListener onProgressListener;

    private OnUploadListener onUploadListener;

    private OnStartListener onStartListener;


    private OnCancelListener onCancelListener;

    private OnUploadFailedListener onUploadFailedListener;

    private OnWaitListener onWaitListener;


    public UploadRequest() {
    }

    UploadRequest(UploadRequestBuilder builder) {

        this.filename = builder.filename;
        this.filesize = builder.filesize;
        this.extention = builder.extention;
        this.checksum = builder.checksum;
        this.thumb = builder.thumb;
        this.deviceId = builder.deviceId;
        this.selectedPath = builder.selectedPath;


        this.priority = builder.priority;
        this.readTimeout =
                builder.readTimeout != 0 ?
                        builder.readTimeout :
                        getReadTimeoutFromConfig();
        this.connectTimeout =
                builder.connectTimeout != 0 ?
                        builder.connectTimeout :
                        getConnectTimeoutFromConfig();
        this.userAgent = builder.userAgent;
        /////////////////////////////////////
        this.status = UploadRequestStatus.UNKNOWN.getType();
        this.progress = 0;
        this.uploadThreadInfos = new ArrayList<>();

    }





    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getExtention() {
        return extention;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSelectedPath() {
        return selectedPath;
    }

    public void setSelectedPath(String selectedPath) {
        this.selectedPath = selectedPath;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

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

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getPartsize() {
        return partsize;
    }

    public void setPartsize(int partsize) {
        this.partsize = partsize;
    }

    public int getTotalpart() {
        return totalpart;
    }

    public void setTotalpart(int totalpart) {
        this.totalpart = totalpart;
    }

    public String getUserAgent() {
        if (userAgent == null || userAgent.length() <= 0) {
            userAgent = ComponentHolder.getInstance().getUserAgent();
        }
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    @NonNull
    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(@NonNull String uploadId) {
        this.uploadId = uploadId;
    }

    public List<UploadThreadInfoModel> getUploadThreadInfos() {
        return uploadThreadInfos;
    }

    public void setUploadThreadInfos(List<UploadThreadInfoModel> uploadThreadInfos) {
        this.uploadThreadInfos = uploadThreadInfos;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        onStatusChanged();
    }


    public OnProgressListener getOnProgressListener() {
        return onProgressListener;
    }

    public UploadRequest setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
        return this;
    }


    public UploadRequest setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }


    public UploadRequest setOnStartListener(OnStartListener onStartListener) {
        this.onStartListener = onStartListener;
        return this;
    }

    public OnStartListener getOnStartListener() {
        return onStartListener;
    }

    public OnUploadListener getOnUploadListener() {
        return onUploadListener;
    }

    public UploadRequest setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
        return this;
    }


    public OnUploadFailedListener getOnUploadFailedListener() {
        return onUploadFailedListener;
    }

    public UploadRequest setOnUploadFailedListener(OnUploadFailedListener onUploadFailedListener) {
        this.onUploadFailedListener = onUploadFailedListener;
        return this;
    }

    public OnWaitListener getOnWaitListener() {
        return onWaitListener;
    }

    public UploadRequest setOnWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
        return this;
    }


    ////////////////////////////////////////////////////////////////
    public String startWithListner(OnUploadListener onUploadListener, Context ctx) {
        this.onUploadListener = onUploadListener;

        uploadId = Utils.getUniqueId(selectedPath, filename);


        UploadRequestQueue.getInstance().addRequest(this, ctx);

        return uploadId;
    }



    private void finish() {
        destroy();
        UploadRequestQueue.getInstance().finish(this);
    }

    private void destroy() {
        this.onProgressListener = null;
        this.onUploadListener = null;
        this.onStartListener = null;
        this.onCancelListener = null;
        this.onUploadFailedListener = null;
        this.onWaitListener = null;
    }

    private int getReadTimeoutFromConfig() {
        return ComponentHolder.getInstance().getReadTimeout();
    }

    private int getConnectTimeoutFromConfig() {
        return ComponentHolder.getInstance().getConnectTimeout();
    }


    public void onStatusChanged() {

        System.out.println("test onStatusChanged     " + this.getStatus());


        switch (this.getStatus()) {
            case 0:
                deliverWaitEvent();
                break;
            case 1:
                deliverStartEvent();
                break;

            case 4:
                deliverSuccess();
                break;
            case 5:
                deliverCancelEvent();
                break;

        }
    }

    public void handleException(UploadException exception) {
        System.out.println("test handleException     " + this.getStatus());

        deliverError(exception);
    }


    public void deliverWaitEvent() {

        System.out.println("test deliverWaitEvent     " + this.getStatus());


        if (status != UploadRequestStatus.CANCELLED.getType()) {
            UploadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnWaitListener() != null) {
                                System.out.println("test getOnWaitListener not null");
                                getOnWaitListener().onWaited();
                            }
                        }
                    });
        }
    }

    public void deliverStartEvent() {
        System.out.println("test deliverStartEvent     " + this.getStatus());
        if (status != UploadRequestStatus.CANCELLED.getType()) {
            UploadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnStartListener() != null) {

                                System.out.println("test getOnStartOrResumeListener not null");
                                getOnStartListener().onStart();
                            }
                        }
                    });
        }
    }


    public void deliverSuccess() {

        System.out.println("test deliverSuccess     " + this.getStatus());


        if (status != UploadRequestStatus.CANCELLED.getType()) {
//            setStatus(UploadRequestStatus.COMPLETED);
            UploadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnUploadListener() != null) {

                                System.out.println("test getOnUploadListener not null");


                                getOnUploadListener().onUploadComplete();
                            }
                            finish();
                        }
                    });
        }

    }

    public void deliverError(final UploadException e) {

        System.out.println("test deliverError     " + this.getStatus());

        if (status != UploadRequestStatus.CANCELLED.getType()) {
            UploadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnUploadFailedListener() != null) {

                                System.out.println("test getOnUploadFailedListener not null");


                                getOnUploadFailedListener().onUploadFailed(e);
                            }
                            finish();
                        }
                    });
        }

    }

    private void deliverCancelEvent() {

        System.out.println("test deliverCancelEvent " + this.getStatus());


        UploadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(new Runnable() {
                    public void run() {
                        if (getOnCancelListener() != null) {

                            System.out.println("test getOnCancelListener not null");

                            getOnCancelListener().onCancel();
                        }

                        finish();
                    }
                });


    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UploadRequest uploadRequest = (UploadRequest) o;

        return this.uploadId == uploadRequest.getUploadId();

    }


    public UploadRequest getCurentObject() {
        return this;
    }
}
