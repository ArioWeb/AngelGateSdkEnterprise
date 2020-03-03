

package com.angelsgate.sdk.AngelsGateDownload.downloader;



public enum DownloadRequestStatus {

    WAIT(0),

    QUEUED(1),//PREPARE_DOWNLOAD

    RUNNING(2),//DOWNLOADING

    PAUSED(3),

    COMPLETED(4),

    CANCELLED(5),//REMOVED

    UNKNOWN(6),//NONE

    ERROR(7);


    private final int mType;

    DownloadRequestStatus(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }


}
