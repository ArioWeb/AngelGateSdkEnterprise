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


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadRequestStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnCancelListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnDownloadFailedListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnDownloadListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnPauseListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnProgressListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnStartOrResumeListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnWaitListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Priority;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Utils.Utils;
import com.angelsgate.sdk.AngelsGateDownload.downloader.core.DownloadCore;
import com.angelsgate.sdk.AngelsGateDownload.downloader.database.DownloadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.DownloadRequestQueue;

import java.util.ArrayList;
import java.util.List;


@Entity
public class DownloadRequest {

    @PrimaryKey
    @NonNull
    private String downloadId;
    private int status;
    private long totalBytes;/////oooooooooo
    private long progress;//////oooooooooooooo
    @Ignore
    private List<DownloadThreadInfoModel> downloadThreadInfos;/////////////oooooooooooooo
    ///////////////////////////////////////////////
    private String handler;
    private String deviceId;
    private String dirPath;
    private String fileName;
    private String fileExtention;
    /////////////////////////////////////////
    @Ignore
    private Priority priority;
    @Ignore
    private int readTimeout;
    @Ignore
    private int connectTimeout;
    @Ignore
    private String userAgent;
    @Ignore
    private int sequenceNumber;
    @Ignore
    private OnProgressListener onProgressListener;
    @Ignore
    private OnDownloadListener onDownloadListener;
    @Ignore
    private OnStartOrResumeListener onStartOrResumeListener;
    @Ignore
    private OnPauseListener onPauseListener;
    @Ignore
    private OnCancelListener onCancelListener;
    @Ignore
    private OnDownloadFailedListener onDownloadFailedListener;
    @Ignore
    private OnWaitListener onWaitListener;


    public DownloadRequest() {
    }

    DownloadRequest(DownloadRequestBuilder builder) {
        this.handler = builder.handler;
        this.dirPath = builder.dirPath;
        this.fileName = builder.fileName;
        this.deviceId = builder.deviceId;
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
        this.status = DownloadRequestStatus.UNKNOWN.getType();
        this.totalBytes = 0;
        this.progress = 0;
        this.downloadThreadInfos = new ArrayList<>();
        this.fileExtention="";
    }


    public String getFileExtention() {
        return fileExtention;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }


    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
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

    public String getUserAgent() {
        if (userAgent == null || userAgent.length() <= 0) {
            userAgent = ComponentHolder.getInstance().getUserAgent();
        }
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public List<DownloadThreadInfoModel> getDownloadThreadInfos() {
        return downloadThreadInfos;
    }

    public void setDownloadThreadInfos(List<DownloadThreadInfoModel> downloadThreadInfos) {
        this.downloadThreadInfos = downloadThreadInfos;
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

    public DownloadRequest setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
        return this;
    }


    public DownloadRequest setOnPauseListener(OnPauseListener onPauseListener) {
        this.onPauseListener = onPauseListener;
        return this;
    }

    public OnPauseListener getOnPauseListener() {
        return onPauseListener;
    }


    public DownloadRequest setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }


    public DownloadRequest setOnStartOrResumeListener(OnStartOrResumeListener onStartOrResumeListener) {
        this.onStartOrResumeListener = onStartOrResumeListener;
        return this;
    }

    public OnStartOrResumeListener getOnStartOrResumeListener() {
        return onStartOrResumeListener;
    }

    public OnDownloadListener getOnDownloadListener() {
        return onDownloadListener;
    }

    public DownloadRequest setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
        return this;
    }


    public OnDownloadFailedListener getOnDownloadFailedListener() {
        return onDownloadFailedListener;
    }

    public DownloadRequest setOnDownloadFailedListener(OnDownloadFailedListener onDownloadFailedListener) {
        this.onDownloadFailedListener = onDownloadFailedListener;
        return this;
    }

    public OnWaitListener getOnWaitListener() {
        return onWaitListener;
    }

    public DownloadRequest setOnWaitListener(OnWaitListener onWaitListener) {
        this.onWaitListener = onWaitListener;
        return this;
    }


    ////////////////////////////////////////////////////////////////
    public String startWithListner(OnDownloadListener onDownloadListener, Context ctx) {
        this.onDownloadListener = onDownloadListener;
        downloadId = Utils.getUniqueId(handler, dirPath, fileName);

        System.out.println("test downloadId "+ downloadId);

      //  boolean IsDownloading = UploadRequestQueue.getInstance().checkIsDownloading(downloadId);

       // System.out.println("test IsDownloading "+ IsDownloading);


//        if (IsDownloading) {
//            OMUploader.resume(downloadId, ctx);
//        } else {
            DownloadRequestQueue.getInstance().addRequest(this, ctx);
       // }

        return downloadId;
    }


//    public String start( Context ctx) {
//        downloadId = Utils.getUniqueId(handler, dirPath, fileName);
//        boolean IsDownloading = UploadRequestQueue.getInstance().checkIsDownloading(downloadId);
//        if (IsDownloading) {
//            OMUploader.resume(downloadId, ctx);
//        } else {
//            UploadRequestQueue.getInstance().addRequest(this, ctx);
//        }
//
//        return downloadId;
//    }


//    public void cancel() {
//        status = UploadRequestStatus.CANCELLED.getType();
//        if (future != null) {
//            future.cancel(true);
//        }
//        deliverCancelEvent();
//        Utils.deleteTempFileAndDatabaseEntryInBackground(Utils.getTempPath(dirPath, fileName), Utils.getTempPartPath(dirPath, fileName), downloadId);
//    }

    private void finish() {
        destroy();
        DownloadRequestQueue.getInstance().finish(this);
    }

    private void destroy() {
        this.onProgressListener = null;
        this.onDownloadListener = null;
        this.onStartOrResumeListener = null;
        this.onPauseListener = null;
        this.onCancelListener = null;
        this.onDownloadFailedListener = null;
        this.onWaitListener = null;
    }

    private int getReadTimeoutFromConfig() {
        return ComponentHolder.getInstance().getReadTimeout();
    }

    private int getConnectTimeoutFromConfig() {
        return ComponentHolder.getInstance().getConnectTimeout();
    }


    public void onStatusChanged() {

        System.out.println("test onStatusChanged     " +this.getStatus());


        //if (this.getStatus() != UploadRequestStatus.CANCELLED.getType()) {
            DownloadCore.getInstance().getExecutorSupplier().forBackgroundTasks()
                    .execute(new Runnable() {
                        public void run() {
                            ComponentHolder.getInstance().getDbHelper().createOrUpdateDownloadRequest(getCurentObject());
                        }
                    });

            if (this.getDownloadThreadInfos() != null && this.getDownloadThreadInfos().size()>0) {

                DownloadCore.getInstance().getExecutorSupplier().forBackgroundTasks()
                        .execute(new Runnable() {
                            public void run() {

                                for (DownloadThreadInfoModel threadInfo : getDownloadThreadInfos()) {
                                    ComponentHolder.getInstance().getDbHelper().createOrUpdateDownloadThreadInfo(threadInfo);
                                }

                            }
                        });

            }


        //}

        switch (this.getStatus()) {
            case 0:
                deliverWaitEvent();
                break;
            case 1:
                deliverStartEvent();
                break;
//            case UploadRequestStatus.RUNNING:
//                if (this.getOnProgressListener() != null) {
//                    this.getOnProgressListener().onProgress(new Progress());
//                }
//                break;
            case 3:
                deliverPauseEvent();
                break;
            case 4:
                deliverSuccess();
                break;
            case 5:
                deliverCancelEvent();
                break;
//            case 7:
//                deliverError(new UploadException(""));
//                break;
        }
    }

    public void handleException(DownloadException exception) {
        System.out.println("test handleException     " +this.getStatus());

        deliverError(exception);
    }


    public void deliverWaitEvent() {

        System.out.println("test deliverWaitEvent     " +this.getStatus());


        if (status != DownloadRequestStatus.CANCELLED.getType()) {
            DownloadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnWaitListener() != null) {
                                System.out.println("test getOnWaitListener not null"  );
                                getOnWaitListener().onWaited();
                            }
                        }
                    });
        }
    }

    public void deliverStartEvent() {
        System.out.println("test deliverStartEvent     " +this.getStatus());
        if (status != DownloadRequestStatus.CANCELLED.getType()) {
            DownloadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnStartOrResumeListener() != null) {

                                System.out.println("test getOnStartOrResumeListener not null"  );
                                getOnStartOrResumeListener().onStartOrResume();
                            }
                        }
                    });
        }
    }

    public void deliverPauseEvent() {
        System.out.println("test deliverPauseEvent     " +this.getStatus());

        if (status != DownloadRequestStatus.CANCELLED.getType()) {
            DownloadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnPauseListener() != null) {

                                System.out.println("test getOnPauseListener not null"  );
                                getOnPauseListener().onPause();
                            }
                        }
                    });
        }
    }

    public void deliverSuccess() {

        System.out.println("test deliverSuccess     " +this.getStatus());


        if (status != DownloadRequestStatus.CANCELLED.getType()) {
//            setStatus(UploadRequestStatus.COMPLETED);
            DownloadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnDownloadListener() != null) {

                                System.out.println("test getOnDownloadListener not null"  );


                                getOnDownloadListener().onDownloadComplete();
                            }
                            finish();
                        }
                    });
        }

    }

    public void deliverError(final DownloadException e) {

        System.out.println("test deliverError     " +this.getStatus());

        if (status != DownloadRequestStatus.CANCELLED.getType()) {
            DownloadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (getOnDownloadFailedListener() != null) {

                                System.out.println("test getOnDownloadFailedListener not null"  );


                                getOnDownloadFailedListener().onDownloadFailed(e);
                            }
                            finish();
                        }
                    });
        }

    }

    private void deliverCancelEvent() {

        System.out.println("test deliverCancelEvent " +this.getStatus());


        DownloadCore.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(new Runnable() {
                    public void run() {
                        if (getOnCancelListener() != null) {

                            System.out.println("test getOnCancelListener not null"  );

                            getOnCancelListener().onCancel();
                        }
                    }
                });

        Utils.deleteTempFileAndDatabaseEntryInBackground(Utils.getTempPath(dirPath, fileName,handler,fileExtention), downloadId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DownloadRequest downloadRequest = (DownloadRequest) o;

        return this.downloadId == downloadRequest.getDownloadId();

    }


    public DownloadRequest getCurentObject() {
        return this;
    }
}
