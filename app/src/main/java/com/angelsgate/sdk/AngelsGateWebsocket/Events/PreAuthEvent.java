package com.angelsgate.sdk.AngelsGateWebsocket.Events;

public class PreAuthEvent {

    boolean status;
    String ResponseBody;

    public PreAuthEvent(boolean status, String responseBody) {
        this.status = status;
        ResponseBody = responseBody;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getResponseBody() {
        return ResponseBody;
    }

    public void setResponseBody(String responseBody) {
        ResponseBody = responseBody;
    }


}
