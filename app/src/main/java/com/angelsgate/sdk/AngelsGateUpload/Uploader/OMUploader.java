package com.angelsgate.sdk.AngelsGateUpload.Uploader;

import android.content.Context;

import com.angelsgate.sdk.AngelsGateUpload.Uploader.core.UploadCore;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.UploadRequestQueue;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequestBuilder;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class OMUploader {

    /**
     * private constructor to prevent instantiation of this class
     */
    private OMUploader() {
    }


    /**
     * Initializes  Uploader with the default config.
     *
     * @param context The context
     */
    public static void initialize(Context context) {
        initialize(context, Config.newBuilder(context).build());
    }

    /**
     * Initializes PRUploader with the custom config.
     *
     * @param context The context
     * @param config  The PRUploaderConfig
     */
    public static void initialize(Context context, Config config) {


        ComponentHolder.getInstance().init(context, config);
        UploadRequestQueue.initialize();


    }


    public static UploadRequestBuilder upload(final String realname, long filesize, String extention, String checksum, String thumb, final String deviceId, String selectedPath) {

        System.out.println("test upload");
        return new UploadRequestBuilder(realname, filesize, extention, checksum, thumb, deviceId, selectedPath);


    }


    /**
     * Method to cancel request with the given uploadId
     *
     * @param uploadId The uploadId with which request is to be cancelled
     */
    public static void cancel(String uploadId, Context context, UploadRequest request) {

        UploadRequestQueue.getInstance().cancel(uploadId, context, request);
    }


    /**
     * Method to cancel all requests
     */
    public static void cancelAll() {

        UploadRequestQueue.getInstance().cancelAll();

    }

    /**
     * Method to check the request with the given uploadId is running or not
     *
     * @param uploadId The uploadId with which request status is to be checked
     * @return the running status
     */
    public static int getStatus(String uploadId) {

        return UploadRequestQueue.getInstance().getStatus(uploadId);

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
     * Shuts PRUploader down
     */
    public static void shutDown() {
        UploadCore.shutDown();
    }


    public static UploadRequest getUploadRequestById(String uploadid) {

        return UploadRequestQueue.getInstance().getUploadRequestById(uploadid);
    }

    public static List<UploadRequest> findAllUploading() {
        return UploadRequestQueue.getInstance().findAllUploading();
    }


}
