package com.angelsgate.sdk.AngelsGateNetwork.model.file;

public class FileUploadSessionPartRequest {

    private String handler;
    private int part;
    private String data;
    private String checksum;


    public FileUploadSessionPartRequest(String handler, int part,String data, String checksum) {
        this.handler = handler;
        this.part = part;

        this.data = data;
        this.checksum = checksum;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
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
