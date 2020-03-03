/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.angelsgate.sdk.AngelsGateDB.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadPart;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadSession;
import com.angelsgate.sdk.AngelsGateDownload.downloader.database.DownloadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;



@Dao
public interface DbHelper {

    @Insert(onConflict = REPLACE)
    void insertUploadSession(UploadSession session);

    @Insert(onConflict = REPLACE)
    void insertUploadPart(UploadPart part);

    @Query("select * from UploadSession where localId = :localId")
    UploadSession loadUploadSessionBylocalId(String localId);


    @Query("select * from UploadPart where handler = :handler and partNumber = :partNumber")
    UploadPart loadUploadPart(String handler, int partNumber);
    ///////////////////////////////////////////////////////////////////////////


    @Query("delete from UploadSession where localId = :localId")
    int deleteUploadSession(String localId);


    @Query("delete from UploadPart where handler = :handler and partNumber = :partNumber")
    int deleteUploadPart(String handler, int partNumber);


    ///////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = REPLACE)
    void insertSocketRequest(SocketRequest request);

    @Query("select * from SocketRequest where segment = :segment")
    SocketRequest loadSocketRequest(int segment);


    @Query("delete from SocketRequest where segment = :segment")
    int deleteSocketRequest(int segment);



    //////////////////////////

    @Query("select * from  DownloadRequest where downloadId = :downloadId")
    DownloadRequest findDownloadRequest(String downloadId);


    @Query("select * from  DownloadRequest where  status != 4 ")
    List<DownloadRequest> findAllDownloading();

    @Query("select * from  DownloadRequest where  status = 4 ")
    List<DownloadRequest> findAllDownloaded();

    @Query("UPDATE  DownloadRequest SET status = 3   WHERE status !=  4 ")
    void pauseAllDownloading();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdateDownloadRequest(DownloadRequest model);

    @Query("delete from  DownloadRequest where downloadId = :downloadId")
    void removeDownloadRequest(String downloadId);


    @Query("delete from  DownloadRequest")
    void clearDownloadRequest();


    ///////////////////////////////////////////////////////////////


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdateDownloadThreadInfo(DownloadThreadInfoModel model);


    @Query("select * from DownloadThreadInfoModel where threadId = :threadId")
    DownloadThreadInfoModel findThreadBythreadId(String threadId);

    @Query("select * from DownloadThreadInfoModel where downloadRequestId = :downloadRequestId")
    List<DownloadThreadInfoModel> findThreadBydownloadRequestId(String downloadRequestId);


    @Query("delete from DownloadThreadInfoModel where downloadRequestId = :downloadRequestId")
    void removeDownloadThread(String downloadRequestId);


    @Query("delete from DownloadThreadInfoModel")
    void clearDownloadThread();


    @Query("UPDATE DownloadThreadInfoModel SET progress = :progress WHERE threadId = :threadId ")
    void updateProgressDownloadThread(String threadId, long progress);



}
