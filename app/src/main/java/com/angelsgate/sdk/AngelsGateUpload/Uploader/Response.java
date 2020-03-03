

package com.angelsgate.sdk.AngelsGateUpload.Uploader;


public class Response {

    private Error error;
    private boolean isSuccessful;
    private boolean isCancelled;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }


    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

}
