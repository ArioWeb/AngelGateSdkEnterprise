package com.angelsgate.sdk.AngelsGateDB.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.angelsgate.sdk.AngelsGateDB.dao.DbHelper;

import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadPart;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadSession;
import com.angelsgate.sdk.AngelsGateDownload.downloader.database.DownloadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;


@Database(entities = {UploadPart.class, UploadSession.class , SocketRequest.class, DownloadRequest.class, DownloadThreadInfoModel.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DbHelper dbModel();


    public static AppDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"angelsgate2")
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE.close();
        INSTANCE = null;
    }

}
