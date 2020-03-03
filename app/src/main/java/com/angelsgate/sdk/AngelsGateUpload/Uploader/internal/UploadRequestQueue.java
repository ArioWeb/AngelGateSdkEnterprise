

package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal;


import android.content.Context;


import com.angelsgate.sdk.AngelsGateUpload.Uploader.UploadRequestStatus;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadRequestQueue implements UploadTask2.UploadTaskListener {

    private static UploadRequestQueue instance;
    private final Map<String, UploadTask2> currentTaskMap;
    private List<UploadRequest> NotCompleteUploadingList;
    private final AtomicInteger sequenceGenerator;

    private UploadRequestQueue() {
        currentTaskMap = new ConcurrentHashMap<>();
        sequenceGenerator = new AtomicInteger();

        if (NotCompleteUploadingList == null ||
                NotCompleteUploadingList.size() <= 0) {
            NotCompleteUploadingList = new ArrayList<>();
        }

    }

    public static void initialize() {
        getInstance();
    }

    public static UploadRequestQueue getInstance() {
        if (instance == null) {
            synchronized (UploadRequestQueue.class) {
                if (instance == null) {
                    instance = new UploadRequestQueue();
                }
            }
        }
        return instance;
    }

    private int getSequenceNumber() {
        return sequenceGenerator.incrementAndGet();
    }

    /////////////////////////////////////////////////////////////////////////////
    public void addRequest(UploadRequest request, Context ctx) {
        NotCompleteUploadingList.add(request);
        System.out.println("test addRequest ");

        prepareUpload(request, ctx);
    }


    private void prepareUpload(UploadRequest request, Context ctx) {
        if (currentTaskMap.size() >= ComponentHolder.getInstance().getUploadTasks()) {
            request.setStatus(UploadRequestStatus.WAIT.getType());

            System.out.println("test prepareUpload  WAIT");


        } else {

            System.out.println("test prepareUpload  QUEUED");

            request.setSequenceNumber(getSequenceNumber());
            UploadTask2 uploadTask = new UploadTask2(request, ctx, this);
            currentTaskMap.put(request.getUploadId(), uploadTask);
            request.setStatus(UploadRequestStatus.QUEUED.getType());
            uploadTask.start();
        }
    }

//    public void resume(String uploadId, Context ctx, UploadRequest request) {
//
//        System.out.println("test resume  resume");
//
//        for (int i = 0; i < NotCompleteUploadingList.size(); i++) {
//            if (NotCompleteUploadingList.get(i).getUploadId().equals(uploadId)) {
//
//                /////////////////////
//                NotCompleteUploadingList.get(i).setOnUploadListener(request.getOnUploadListener());
//                NotCompleteUploadingList.get(i).setOnStartListener(request.getOnStartListener());
//                NotCompleteUploadingList.get(i).setOnCancelListener(request.getOnCancelListener());
//                NotCompleteUploadingList.get(i).setOnProgressListener(request.getOnProgressListener());
//                NotCompleteUploadingList.get(i).setOnUploadFailedListener(request.getOnUploadFailedListener());
//                NotCompleteUploadingList.get(i).setOnWaitListener(request.getOnWaitListener());
//
//                ///////////////////////////
//
//                prepareUpload(NotCompleteUploadingList.get(i), ctx);
//                break;
//            }
//        }
//
//    }


    private void prepareUploadNextTask(Context ctx) {
        for (UploadRequest request : NotCompleteUploadingList) {
            if (request.getStatus() == UploadRequestStatus.WAIT.getType()) {
                prepareUpload(request, ctx);
                break;
            }
        }
    }


//    public void resumeAll(Context ctx) {
//
//        for (UploadRequest request : NotCompleteUploadingList) {
//            prepareUpload(request, ctx);
//        }
//
//    }


//    public void pauseAll(Context ctx) {
//        for (UploadRequest request : NotCompleteUploadingList) {
//            currentTaskMap.remove(request.getUploadId());
//            request.setStatus(UploadRequestStatus.PAUSED.getType());
//        }
//
//    }
//
//    public void pause(String uploadId, Context ctx, UploadRequest request) {
//        currentTaskMap.remove(uploadId);
//
//        for (int i = 0; i < NotCompleteUploadingList.size(); i++) {
//            if (NotCompleteUploadingList.get(i).getUploadId().equals(uploadId)) {
//
//                /////////////////////
//                NotCompleteUploadingList.get(i).setOnUploadListener(request.getOnUploadListener());
//                NotCompleteUploadingList.get(i).setOnStartOrResumeListener(request.getOnStartOrResumeListener());
//                NotCompleteUploadingList.get(i).setOnPauseListener(request.getOnPauseListener());
//                NotCompleteUploadingList.get(i).setOnCancelListener(request.getOnCancelListener());
//                NotCompleteUploadingList.get(i).setOnProgressListener(request.getOnProgressListener());
//                NotCompleteUploadingList.get(i).setOnUploadFailedListener(request.getOnUploadFailedListener());
//                NotCompleteUploadingList.get(i).setOnWaitListener(request.getOnWaitListener());
//
//                ///////////////////////////
//                NotCompleteUploadingList.get(i).setStatus(UploadRequestStatus.PAUSED.getType());
//                break;
//            }
//        }
//        prepareUploadNextTask(ctx);
//    }


    public void cancel(String uploadId, Context ctx, UploadRequest request) {


        for (int i = 0; i < NotCompleteUploadingList.size(); i++) {

            if (NotCompleteUploadingList.get(i).getUploadId().equals(uploadId)) {
                /////////////////////
                NotCompleteUploadingList.get(i).setOnUploadListener(request.getOnUploadListener());
                NotCompleteUploadingList.get(i).setOnStartListener(request.getOnStartListener());

                NotCompleteUploadingList.get(i).setOnCancelListener(request.getOnCancelListener());
                NotCompleteUploadingList.get(i).setOnProgressListener(request.getOnProgressListener());
                NotCompleteUploadingList.get(i).setOnUploadFailedListener(request.getOnUploadFailedListener());
                NotCompleteUploadingList.get(i).setOnWaitListener(request.getOnWaitListener());

                ///////////////////////////

                NotCompleteUploadingList.get(i).setStatus(UploadRequestStatus.CANCELLED.getType());
                NotCompleteUploadingList.remove(i);
                break;
            }

        }

        currentTaskMap.remove(uploadId);
    }


    public void cancelAll() {

        for (int i = 0; i < NotCompleteUploadingList.size(); i++) {

            NotCompleteUploadingList.get(i).setStatus(UploadRequestStatus.CANCELLED.getType());
            currentTaskMap.remove(NotCompleteUploadingList.get(i).getUploadId());
            NotCompleteUploadingList.remove(i);
        }

    }


    public List<UploadRequest> findAllUploading() {
        return NotCompleteUploadingList;
    }





    public UploadRequest getUploadRequestById(String id) {
        UploadRequest uploadInfo = null;
        for (UploadRequest d : NotCompleteUploadingList) {
            if (d.getUploadId().equals(id)) {
                uploadInfo = d;
                break;
            }
        }
        return uploadInfo;
    }


    public int getStatus(String uploadId) {
        UploadRequest Mainrequest = null;

        for (UploadRequest request : NotCompleteUploadingList) {
            if (request.getUploadId().equals(uploadId)) {
                Mainrequest = request;
                break;
            }
        }

        if (Mainrequest == null) {
             return UploadRequestStatus.UNKNOWN.getType();
        }
        return Mainrequest.getStatus();
    }


    public void finish(UploadRequest request) {
        currentTaskMap.remove(request.getUploadId());
    }

    public boolean checkIsUploading(String uploadId) {
        for (int i = 0; i < NotCompleteUploadingList.size(); i++) {
            if (NotCompleteUploadingList.get(i).getUploadId().equals(uploadId)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public void onUploadSuccess(UploadRequest request, Context ctx) {
        System.out.println("test  UploadRequestQueue onUploadSuccess  ");


        currentTaskMap.remove(request.getUploadId());
        NotCompleteUploadingList.remove(request);
        prepareUploadNextTask(ctx);
    }
}
