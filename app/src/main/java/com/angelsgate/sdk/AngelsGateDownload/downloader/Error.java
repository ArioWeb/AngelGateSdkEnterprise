

package com.angelsgate.sdk.AngelsGateDownload.downloader;


public class Error {

    private boolean isServerError;
    private boolean isConnectionError;

    public boolean isServerError() {
        return isServerError;
    }

    public void setServerError(boolean serverError) {
        isServerError = serverError;
    }

    public boolean isConnectionError() {
        return isConnectionError;
    }

    public void setConnectionError(boolean connectionError) {
        isConnectionError = connectionError;
    }
}
