package com.angelsgate.sdk.AngelsGateNetwork.model.file.download;

public class GetFileInfoResponse {
    String time ;
    String realname ;
    long partsize ;
    long size ;
    int partnum ;
    String status ;
    String checksum ;
    String extension ;
    String thumb ;

    public GetFileInfoResponse(String time, String realname, long partsize, long size, int partnum, String status, String checksum, String extension, String thumb) {
        this.time = time;
        this.realname = realname;
        this.partsize = partsize;
        this.size = size;
        this.partnum = partnum;
        this.status = status;
        this.checksum = checksum;
        this.extension = extension;
        this.thumb = thumb;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public long getPartsize() {
        return partsize;
    }

    public void setPartsize(long partsize) {
        this.partsize = partsize;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getPartnum() {
        return partnum;
    }

    public void setPartnum(int partnum) {
        this.partnum = partnum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
