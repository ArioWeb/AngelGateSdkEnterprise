

package com.angelsgate.sdk.AngelsGateDB;


import android.content.Context;

import com.angelsgate.sdk.AngelsGateDB.dao.DbHelper;
import com.angelsgate.sdk.AngelsGateDB.database.AppDatabase;
import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadPart;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadSession;
import com.angelsgate.sdk.AngelsGateDownload.downloader.database.DownloadThreadInfoModel;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;

import java.util.ArrayList;
import java.util.List;


public class AppDbHelper implements DbHelper {

    Context context;

    AppDatabase db;

    public AppDbHelper(Context context) {
        this.context = context;
        db = AppDatabase.getInMemoryDatabase(context);
    }


    @Override
    public  void insertUploadSession(UploadSession session) {
        db.dbModel().insertUploadSession(session);
    }

    @Override
    public void insertUploadPart(UploadPart part) {
        db.dbModel().insertUploadPart(part);
    }

    @Override
    public UploadSession loadUploadSessionBylocalId(String localId) {
        return db.dbModel().loadUploadSessionBylocalId(localId);
    }

    @Override
    public UploadPart loadUploadPart(String handler, int partNumber) {
        return db.dbModel().loadUploadPart(handler, partNumber);
    }

    @Override
    public int deleteUploadSession(String localId) {
        return db.dbModel().deleteUploadSession(localId);
    }

    @Override
    public int deleteUploadPart(String handler, int partNumber) {
        return db.dbModel().deleteUploadPart(handler, partNumber);
    }

    @Override
    public void insertSocketRequest(SocketRequest request) {
        db.dbModel().insertSocketRequest(request);
    }

    @Override
    public SocketRequest loadSocketRequest(int segment) {
        System.out.println("data_response00000000 ");

        return db.dbModel().loadSocketRequest(segment) ;
    }

    @Override
    public int deleteSocketRequest(int segment) {
        return db.dbModel().deleteSocketRequest(segment);
    }


    ///////////////////////////////


    @Override
    public DownloadRequest findDownloadRequest(String downloadId) {
        DownloadRequest request = db.dbModel().findDownloadRequest(downloadId);

        if (request != null) {
            List<DownloadThreadInfoModel> DownloadThreads = findThreadBydownloadRequestId(request.getDownloadId());
            request.setDownloadThreadInfos(DownloadThreads);
        }


        return request;
    }

    @Override
    public List<DownloadRequest> findAllDownloading() {
        List<DownloadRequest> requests = db.dbModel().findAllDownloading();


        if (requests != null && requests.size() > 0) {
            for (DownloadRequest req : requests) {
                List<DownloadThreadInfoModel> DownloadThreads = findThreadBydownloadRequestId(req.getDownloadId());
                req.setDownloadThreadInfos(DownloadThreads);
            }
        } else {
            requests = new ArrayList<>();
        }


        return requests;
    }

    @Override
    public List<DownloadRequest> findAllDownloaded() {
        List<DownloadRequest> requests = db.dbModel().findAllDownloaded();

        if (requests != null && requests.size() > 0) {

            for (DownloadRequest req : requests) {
                List<DownloadThreadInfoModel> DownloadThreads = findThreadBydownloadRequestId(req.getDownloadId());
                req.setDownloadThreadInfos(DownloadThreads);
            }

        } else {
            requests = new ArrayList<>();
        }


        return requests;
    }

    @Override
    public void pauseAllDownloading() {
        db.dbModel().pauseAllDownloading();
    }


    @Override
    public void createOrUpdateDownloadRequest(DownloadRequest model) {
        db.dbModel().createOrUpdateDownloadRequest(model);
    }

    @Override
    public void removeDownloadRequest(String downloadId) {
        db.dbModel().removeDownloadRequest(downloadId);
        db.dbModel().removeDownloadThread(downloadId);
    }

    @Override
    public void clearDownloadRequest() {
        db.dbModel().clearDownloadRequest();
        db.dbModel().clearDownloadThread();
    }

    @Override
    public void createOrUpdateDownloadThreadInfo(DownloadThreadInfoModel model) {
        db.dbModel().createOrUpdateDownloadThreadInfo(model);
    }

    @Override
    public DownloadThreadInfoModel findThreadBythreadId(String threadId) {
        return db.dbModel().findThreadBythreadId(threadId);
    }

    @Override
    public List<DownloadThreadInfoModel> findThreadBydownloadRequestId(String downloadRequestId) {
        return db.dbModel().findThreadBydownloadRequestId(downloadRequestId);
    }

    @Override
    public void removeDownloadThread(String downloadRequestId) {
        db.dbModel().removeDownloadThread(downloadRequestId);
    }

    @Override
    public void clearDownloadThread() {
        db.dbModel().clearDownloadThread();
    }


    @Override
    public void updateProgressDownloadThread(String threadId, long progress) {
        db.dbModel().updateProgressDownloadThread(threadId, progress);
    }

}
