

package com.angelsgate.sdk.AngelsGateDownload.downloader;


public enum DownloadThreadStatus {


    RUNNING(0),//DOWNLOADING


    COMPLETED(1),


    ERROR(2);


    private final int mType;

    DownloadThreadStatus(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }


}
