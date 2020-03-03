/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.angelsgate.sdk.AngelsGateDownload.downloader.internal;


import android.content.Context;

import com.angelsgate.sdk.AngelsGateDownload.downloader.DownloadRequestStatus;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class DownloadRequestQueue implements DownloadTask2.DownloadTaskListener {

    private static DownloadRequestQueue instance;
    private final Map<String, DownloadTask2> currentTaskMap;
    private List<DownloadRequest> NotCompleteDownloadingList;
    private final AtomicInteger sequenceGenerator;

    private DownloadRequestQueue() {
        currentTaskMap = new ConcurrentHashMap<>();
        sequenceGenerator = new AtomicInteger();
        NotCompleteDownloadingList = ComponentHolder.getInstance().getDbHelper().findAllDownloading();

        if (NotCompleteDownloadingList == null ||
                NotCompleteDownloadingList.size() <= 0) {
            NotCompleteDownloadingList = new ArrayList<>();
        }

    }

    public static void initialize() {
        getInstance();
    }

    public static DownloadRequestQueue getInstance() {
        if (instance == null) {
            synchronized (DownloadRequestQueue.class) {
                if (instance == null) {
                    instance = new DownloadRequestQueue();
                }
            }
        }
        return instance;
    }

    private int getSequenceNumber() {
        return sequenceGenerator.incrementAndGet();
    }

    /////////////////////////////////////////////////////////////////////////////
    public void addRequest(DownloadRequest request, Context ctx) {
        NotCompleteDownloadingList.add(request);
        System.out.println("test addRequest ");

        prepareDownload(request, ctx);
    }


    private void prepareDownload(DownloadRequest request, Context ctx) {
        if (currentTaskMap.size() >= ComponentHolder.getInstance().getDownloadTasks()) {
            request.setStatus(DownloadRequestStatus.WAIT.getType());

            System.out.println("test prepareDownload  WAIT");


        } else {

            System.out.println("test prepareDownload  QUEUED");

            request.setSequenceNumber(getSequenceNumber());
            DownloadTask2 downloadTask = new DownloadTask2(request, ctx, this);
            currentTaskMap.put(request.getDownloadId(), downloadTask);
            request.setStatus(DownloadRequestStatus.QUEUED.getType());
            downloadTask.start();
        }
    }

    public void resume(String downloadId, Context ctx, DownloadRequest request) {

        System.out.println("test resume  resume");

        for (int i = 0; i < NotCompleteDownloadingList.size(); i++) {
            if (NotCompleteDownloadingList.get(i).getDownloadId().equals(downloadId)) {

                /////////////////////
                NotCompleteDownloadingList.get(i).setOnDownloadListener(request.getOnDownloadListener());
                NotCompleteDownloadingList.get(i).setOnStartOrResumeListener(request.getOnStartOrResumeListener());
                NotCompleteDownloadingList.get(i).setOnPauseListener(request.getOnPauseListener());
                NotCompleteDownloadingList.get(i).setOnCancelListener(request.getOnCancelListener());
                NotCompleteDownloadingList.get(i).setOnProgressListener(request.getOnProgressListener());
                NotCompleteDownloadingList.get(i).setOnDownloadFailedListener(request.getOnDownloadFailedListener());
                NotCompleteDownloadingList.get(i).setOnWaitListener(request.getOnWaitListener());

                ///////////////////////////

                prepareDownload(NotCompleteDownloadingList.get(i), ctx);
                break;
            }
        }

    }


    private void prepareDownloadNextTask(Context ctx) {
        for (DownloadRequest request : NotCompleteDownloadingList) {
            if (request.getStatus() == DownloadRequestStatus.WAIT.getType()) {
                prepareDownload(request, ctx);
                break;
            }
        }
    }


    public void resumeAll(Context ctx) {

        for (DownloadRequest request : NotCompleteDownloadingList) {
            prepareDownload(request, ctx);
        }

    }


    public void pauseAll(Context ctx) {
        for (DownloadRequest request : NotCompleteDownloadingList) {
            currentTaskMap.remove(request.getDownloadId());
            request.setStatus(DownloadRequestStatus.PAUSED.getType());
        }

    }

    public void pause(String downloadId, Context ctx, DownloadRequest request) {
        currentTaskMap.remove(downloadId);

        for (int i = 0; i < NotCompleteDownloadingList.size(); i++) {
            if (NotCompleteDownloadingList.get(i).getDownloadId().equals(downloadId)) {

                /////////////////////
                NotCompleteDownloadingList.get(i).setOnDownloadListener(request.getOnDownloadListener());
                NotCompleteDownloadingList.get(i).setOnStartOrResumeListener(request.getOnStartOrResumeListener());
                NotCompleteDownloadingList.get(i).setOnPauseListener(request.getOnPauseListener());
                NotCompleteDownloadingList.get(i).setOnCancelListener(request.getOnCancelListener());
                NotCompleteDownloadingList.get(i).setOnProgressListener(request.getOnProgressListener());
                NotCompleteDownloadingList.get(i).setOnDownloadFailedListener(request.getOnDownloadFailedListener());
                NotCompleteDownloadingList.get(i).setOnWaitListener(request.getOnWaitListener());

                ///////////////////////////
                NotCompleteDownloadingList.get(i).setStatus(DownloadRequestStatus.PAUSED.getType());
                break;
            }
        }
        prepareDownloadNextTask(ctx);
    }


    public void cancel(String downloadId, Context ctx, DownloadRequest request) {


        for (int i = 0; i < NotCompleteDownloadingList.size(); i++) {

            if (NotCompleteDownloadingList.get(i).getDownloadId().equals(downloadId)) {
                /////////////////////
                NotCompleteDownloadingList.get(i).setOnDownloadListener(request.getOnDownloadListener());
                NotCompleteDownloadingList.get(i).setOnStartOrResumeListener(request.getOnStartOrResumeListener());
                NotCompleteDownloadingList.get(i).setOnPauseListener(request.getOnPauseListener());
                NotCompleteDownloadingList.get(i).setOnCancelListener(request.getOnCancelListener());
                NotCompleteDownloadingList.get(i).setOnProgressListener(request.getOnProgressListener());
                NotCompleteDownloadingList.get(i).setOnDownloadFailedListener(request.getOnDownloadFailedListener());
                NotCompleteDownloadingList.get(i).setOnWaitListener(request.getOnWaitListener());

                ///////////////////////////

                NotCompleteDownloadingList.get(i).setStatus(DownloadRequestStatus.CANCELLED.getType());
                NotCompleteDownloadingList.remove(i);
                break;
            }

        }

        currentTaskMap.remove(downloadId);
    }


    public void cancelAll() {

        for (int i = 0; i < NotCompleteDownloadingList.size(); i++) {

            NotCompleteDownloadingList.get(i).setStatus(DownloadRequestStatus.CANCELLED.getType());
            currentTaskMap.remove(NotCompleteDownloadingList.get(i).getDownloadId());
            NotCompleteDownloadingList.remove(i);
        }

    }


    public List<DownloadRequest> findAllDownloading() {
        return NotCompleteDownloadingList;
    }


    public List<DownloadRequest> findAllDownloaded() {
        return ComponentHolder.getInstance().getDbHelper().findAllDownloaded();
    }


    public DownloadRequest getDownloadRequestById(String id) {
        DownloadRequest downloadInfo = null;
        for (DownloadRequest d : NotCompleteDownloadingList) {
            if (d.getDownloadId().equals(id)) {
                downloadInfo = d;
                break;
            }
        }

        if (downloadInfo == null) {
            downloadInfo = ComponentHolder.getInstance().getDbHelper().findDownloadRequest(id);
        }
        return downloadInfo;
    }


    public int getStatus(String downloadId) {
        DownloadRequest Mainrequest = null;

        for (DownloadRequest request : NotCompleteDownloadingList) {
            if (request.getDownloadId().equals(downloadId)) {
                Mainrequest = request;
                break;
            }
        }

        if (Mainrequest == null) {
            Mainrequest = ComponentHolder.getInstance().getDbHelper().findDownloadRequest(downloadId);
        }
        return Mainrequest.getStatus();
    }


    public void finish(DownloadRequest request) {
        currentTaskMap.remove(request.getDownloadId());
    }

    public boolean checkIsDownloading(String downloadId) {
        for (int i = 0; i < NotCompleteDownloadingList.size(); i++) {
            if (NotCompleteDownloadingList.get(i).getDownloadId().equals(downloadId)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public void onDownloadSuccess(DownloadRequest request, Context ctx) {
        System.out.println("test  UploadRequestQueue onDownloadSuccess  ");


        currentTaskMap.remove(request.getDownloadId());
        NotCompleteDownloadingList.remove(request);
        prepareDownloadNextTask(ctx);
    }
}
