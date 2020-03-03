package com.angelsgate.sdk.AngelsGateNetwork.model.file;

public class UploadSessionRequest {
    String realname;
    long size; ///fileSize
    String extension;
    String checksum;
    String thumb; ///base64    ///nadasht khali


    public UploadSessionRequest(String realname, long size, String extention, String checksum, String thumb) {
        this.realname = realname;
        this.size = size;
        this.extension = extention;
        this.checksum = checksum;
        this.thumb = thumb;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtention() {
        return extension;
    }

    public void setExtention(String extention) {
        this.extension = extention;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
