package com.angelsgate.sdk.AngelsGateDB.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SocketRequest {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    public  int segment;
    public  String Ssalt;
    public   String methodName;

    public SocketRequest() {
    }

    public SocketRequest(int segment, String ssalt, String methodName) {
        this.segment = segment;
        Ssalt = ssalt;
        this.methodName = methodName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSegment() {
        return segment;
    }

    public void setSegment(int segment) {
        this.segment = segment;
    }

    public String getSsalt() {
        return Ssalt;
    }

    public void setSsalt(String ssalt) {
        Ssalt = ssalt;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
