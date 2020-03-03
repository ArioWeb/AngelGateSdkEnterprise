

package com.angelsgate.sdk.AngelsGateDownload.downloader;


public final class Constants {

    private Constants() {
        // no instance
    }

    public static final int UPDATE = 0x01;
    public static final String DEFAULT_USER_AGENT = "OMUploader";

    public static final int DEFAULT_READ_TIMEOUT_IN_MILLS = 20000;
    public static final int DEFAULT_CONNECT_TIMEOUT_IN_MILLS = 20000;


    public static final int downloadTasks = 1;
    public static final int downloadThread = 5;
    public static final int retryDownloadThreadCount = 10;


}
