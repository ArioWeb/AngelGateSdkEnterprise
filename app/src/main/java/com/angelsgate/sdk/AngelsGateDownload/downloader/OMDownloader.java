package com.angelsgate.sdk.AngelsGateDownload.downloader;

import android.content.Context;


import com.angelsgate.sdk.AngelsGateDownload.downloader.core.DownloadCore;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.DownloadRequestQueue;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequestBuilder;
import com.angelsgate.sdk.AngelsGateUtils.StorageUtils;


import java.io.File;
import java.util.List;


public class OMDownloader {

    /**
     * private constructor to prevent instantiation of this class
     */
    private OMDownloader() {
    }


    /**
     * Initializes  Downloader with the default config.
     *
     * @param context The context
     */
    public static void initialize(Context context) {
        initialize(context, Config.newBuilder(context).build());
    }

    /**
     * Initializes PRDownloader with the custom config.
     *
     * @param context The context
     * @param config  The PRDownloaderConfig
     */
    public static void initialize(Context context, Config config) {


        ComponentHolder.getInstance().init(context, config);
        DownloadRequestQueue.initialize();


    }

    /**
     * Method to make download request
     *
     * @param handler  The url on which request is to be made
     * @param dirPath  The directory path on which file is to be saved
     * @param fileName The file name with which file is to be saved
     * @return the UploadRequestBuilder
     */
    public static DownloadRequestBuilder download(String handler, String deviceId, String dirPath, String fileName) {

        return new DownloadRequestBuilder(handler, deviceId, dirPath, fileName);


    }

    /**
     * Method to pause request with the given downloadId
     *
     * @param downloadId The downloadId with which request is to be paused
     */

    public static void pause(String downloadId, Context context, DownloadRequest request) {

        DownloadRequestQueue.getInstance().pause(downloadId, context,request);
    }

    /**
     * Method to resume request with the given downloadId
     *
     * @param downloadId The downloadId with which request is to be resumed
     */
    public static void resume(String downloadId, Context ctx, DownloadRequest request) {

        DownloadRequestQueue.getInstance().resume(downloadId, ctx,request);
    }

    /**
     * Method to cancel request with the given downloadId
     *
     * @param downloadId The downloadId with which request is to be cancelled
     */
    public static void cancel(String downloadId, Context context, DownloadRequest request) {

        DownloadRequestQueue.getInstance().cancel(downloadId,context,request);
    }



    /**
     * Method to cancel all requests
     */
    public static void cancelAll() {

        DownloadRequestQueue.getInstance().cancelAll();

    }

    /**
     * Method to check the request with the given downloadId is running or not
     *
     * @param downloadId The downloadId with which request status is to be checked
     * @return the running status
     */
    public static int getStatus(String downloadId) {

        return DownloadRequestQueue.getInstance().getStatus(downloadId);

    }

    /**
     * Method to clean up temporary resumed files which is older than the given day
     *
     * @param days the days
     */
    public static void cleanUp(int days) {
       // Utils.deleteUnwantedModelsAndTempFiles(days);
    }

    /**
     * Shuts PRDownloader down
     */
    public static void shutDown() {
        DownloadCore.shutDown();
    }


    public static DownloadRequest getDownloadRequestById(String downloadid) {

        return DownloadRequestQueue.getInstance().getDownloadRequestById(downloadid);
    }

    public static List<DownloadRequest> findAllDownloading() {
        return DownloadRequestQueue.getInstance().findAllDownloading();
    }

    public static List<DownloadRequest> findAllDownloaded() {
        return DownloadRequestQueue.getInstance().findAllDownloaded();
    }


    public static void resumeAll(Context ctx) {
        DownloadRequestQueue.getInstance().resumeAll(ctx);
    }

    public static void pauseAll(Context ctx) {
        DownloadRequestQueue.getInstance().pauseAll(ctx);

    }


}
