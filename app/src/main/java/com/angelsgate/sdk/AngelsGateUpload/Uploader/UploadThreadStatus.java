

package com.angelsgate.sdk.AngelsGateUpload.Uploader;


public enum UploadThreadStatus {


    RUNNING(0),//UPLOADING


    COMPLETED(1),


    ERROR(2);


    private final int mType;

    UploadThreadStatus(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }


}
