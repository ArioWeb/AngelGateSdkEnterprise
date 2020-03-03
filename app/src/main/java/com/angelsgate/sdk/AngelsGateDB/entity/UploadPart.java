package com.angelsgate.sdk.AngelsGateDB.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UploadPart {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    String handler;
    int partNumber;
    String data;

    public UploadPart(String handler, int partNumber, String data) {
        this.handler = handler;
        this.partNumber = partNumber;
        this.data = data;
    }


    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
