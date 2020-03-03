package com.angelsgate.sdk.AngelsGateDownload.downloader.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DownloadThreadInfoModel {

    @PrimaryKey
    @NonNull
    private String threadId;////downloadRequestId+ part number thread
    private String downloadRequestId;
    private String handler;
    private int partNum;
    private long partSize;
    private long progress;
    int  DownloadThreadStatus;

    public DownloadThreadInfoModel(String threadId, String downloadRequestId, String handler, int partNum, long partSize, long progress,int DownloadThreadStatus) {
        this.threadId = threadId;
        this.downloadRequestId = downloadRequestId;
        this.handler = handler;
        this.partNum = partNum;
        this.partSize = partSize;
        this.progress = progress;
        this.DownloadThreadStatus = DownloadThreadStatus;
    }


    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getDownloadRequestId() {
        return downloadRequestId;
    }

    public void setDownloadRequestId(String downloadRequestId) {
        this.downloadRequestId = downloadRequestId;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }

    public long getPartSize() {
        return partSize;
    }

    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }


    public int getDownloadThreadStatus() {
        return DownloadThreadStatus;
    }

    public void setDownloadThreadStatus(int downloadThreadStatus) {
        DownloadThreadStatus = downloadThreadStatus;
    }
}
