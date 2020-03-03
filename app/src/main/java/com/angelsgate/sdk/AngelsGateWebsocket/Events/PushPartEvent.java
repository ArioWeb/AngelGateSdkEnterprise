package com.angelsgate.sdk.AngelsGateWebsocket.Events;

public class PushPartEvent {

    boolean status;
    String ResponseBody;

    public PushPartEvent(boolean status, String responseBody) {
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
