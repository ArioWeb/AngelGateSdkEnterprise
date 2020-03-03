

package com.angelsgate.sdk.AngelsGateUpload.Uploader;


public enum UploadRequestStatus {

    WAIT(0),

    QUEUED(1),//PREPARE_UPLOAD

    RUNNING(2),//UPLOADING


    COMPLETED(4),

    CANCELLED(5),//REMOVED

    UNKNOWN(6),//NONE

    ERROR(7);


    private final int mType;

    UploadRequestStatus(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }


}
