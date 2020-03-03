package com.angelsgate.sdk.AngelsGateUpload.Uploader.network;

import androidx.annotation.NonNull;

import androidx.room.PrimaryKey;


public class UploadThreadInfoModel {

    private String threadId;////uploadRequestId+ part number thread
    private String handler;////uploadRequestId+ part number thread
    private String uploadRequestId;
    private int partNum;
    private long partSize;
    private long progress;
    int  UploadThreadStatus;

    public UploadThreadInfoModel(String threadId, String uploadRequestId,String handler,  int partNum, long partSize, long progress, int UploadThreadStatus) {
        this.threadId = threadId;
        this.uploadRequestId = uploadRequestId;
        this.handler = handler;
        this.partNum = partNum;
        this.partSize = partSize;
        this.progress = progress;
        this.UploadThreadStatus = UploadThreadStatus;
    }


    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getUploadRequestId() {
        return uploadRequestId;
    }

    public void setUploadRequestId(String uploadRequestId) {
        this.uploadRequestId = uploadRequestId;
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


    public int getUploadThreadStatus() {
        return UploadThreadStatus;
    }

    public void setUploadThreadStatus(int uploadThreadStatus) {
        UploadThreadStatus = uploadThreadStatus;
    }
}
