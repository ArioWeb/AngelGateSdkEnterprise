

package com.angelsgate.sdk.AngelsGateUpload.Uploader;


public final class Constants {

    private Constants() {
        // no instance
    }

    public static final int UPDATE = 0x01;
    public static final String DEFAULT_USER_AGENT = "OMUploader";

    public static final int DEFAULT_READ_TIMEOUT_IN_MILLS = 20000;
    public static final int DEFAULT_CONNECT_TIMEOUT_IN_MILLS = 20000;


    public static final int uploadTasks = 1;
    public static final int uploadThread = 5;
    public static final int retryuploadThreadCount = 10;


}
