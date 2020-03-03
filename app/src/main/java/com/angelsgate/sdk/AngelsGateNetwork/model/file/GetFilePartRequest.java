package com.angelsgate.sdk.AngelsGateNetwork.model.file;

public class GetFilePartRequest {

    String handler;
    String part;

    public GetFilePartRequest(String handler, String part) {
        this.handler = handler;
        this.part = part;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
