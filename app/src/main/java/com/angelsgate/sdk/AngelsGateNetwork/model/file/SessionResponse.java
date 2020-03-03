package com.angelsgate.sdk.AngelsGateNetwork.model.file;

public class SessionResponse {

    String handler;

    int partsize;

    int totalpart;



    public SessionResponse(String handler, int partsize, int totalpart) {
        this.handler = handler;
        this.partsize = partsize;
        this.totalpart = totalpart;
    }



    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getPartsize() {
        return partsize;
    }

    public void setPartsize(int partsize) {
        this.partsize = partsize;
    }

    public int getTotalpart() {
        return totalpart;
    }

    public void setTotalpart(int totalpart) {
        this.totalpart = totalpart;
    }
}
