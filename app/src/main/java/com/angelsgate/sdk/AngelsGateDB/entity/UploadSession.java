package com.angelsgate.sdk.AngelsGateDB.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UploadSession {




    @PrimaryKey
    @NonNull
    String localId;

    String thumb;

    public UploadSession(@NonNull String localId, String thumb) {
        this.localId = localId;
        this.thumb = thumb;
    }

    @NonNull
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(@NonNull String localId) {
        this.localId = localId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
