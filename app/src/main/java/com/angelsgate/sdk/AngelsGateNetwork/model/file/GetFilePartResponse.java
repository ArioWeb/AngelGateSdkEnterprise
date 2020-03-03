package com.angelsgate.sdk.AngelsGateNetwork.model.file;

public class GetFilePartResponse {
    String part;
    String Handler;
    String data;
    String checksum;


    public GetFilePartResponse(String part, String handler, String data, String checksum) {
        this.part = part;
        Handler = handler;
        this.data = data;
        this.checksum = checksum;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getHandler() {
        return Handler;
    }

    public void setHandler(String handler) {
        Handler = handler;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
