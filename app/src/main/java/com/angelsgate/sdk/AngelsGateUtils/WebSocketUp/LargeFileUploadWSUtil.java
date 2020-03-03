package com.angelsgate.sdk.AngelsGateUtils.WebSocketUp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.angelsgate.sdk.AngelsGateNetwork.model.file.SessionResponse;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobManager;
import com.angelsgate.sdk.jobs.SocketJobs.CreateSessionWSJob;

import com.angelsgate.sdk.service.uploadHelperWSService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;


public class LargeFileUploadWSUtil {

    Context CTX;

    /**
     * Creates a LargeFileUpload object.
     *
     * @param nParallelConnections number of parallel http connections to use
     * @param timeOut              time to wait before killing the job
     * @param unit                 time unit for the time wait value
     */


    /**
     * Creates a LargeFileUpload object with a default number of parallel conections and timeout.
     */
    public LargeFileUploadWSUtil(Context context) {
        this.CTX = context;
    }


    public void uploadFile(InputStream stream,
                           String realname, long filesize, String extention, String checksum, String thumb, String deviceId,String selectedPath) throws InterruptedException, IOException {
        //Create a upload session
        this.createUploadSession(stream, realname, filesize, extention, checksum, thumb, deviceId,selectedPath);

    }


    public void createUploadSession(final InputStream stream, String realname, final long size, String extention, String checksum, String thumb, final String deviceId, final String selectedPath) {

        JobManager jobmanager = new JobManager(CTX, WorkManager.getInstance());


        Data.Builder dataBuilder = CreateSessionWSJob.constructData(realname, size, extention, checksum, thumb, deviceId);


        OneTimeWorkRequest Jobrequest = jobmanager.add(CreateSessionWSJob.class, CreateSessionWSJob.constructParameters(), dataBuilder);


        WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                .observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished()) {
                            String StatusResult = info.getOutputData().getString(CreateSessionWSJob.KEY_RESULT);

                            if (StatusResult != null) {


                                switch (StatusResult) {

                                    case "success":


                                        String itemsJson = info.getOutputData().getString(CreateSessionWSJob.KEY_RESPONSE);
                                        Gson convertor = new Gson();
                                        SessionResponse itemsObject = convertor.fromJson(itemsJson, SessionResponse.class);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {



                                        } else {

                                            Intent background1 = new Intent(CTX, uploadHelperWSService.class);
                                            uploadHelperWSService.setFileSize(size);
                                            uploadHelperWSService.setSession(itemsObject);
                                            uploadHelperWSService.setStream(stream);
                                            uploadHelperWSService.setSelectedPath(selectedPath);
                                            uploadHelperWSService.setdeviceId(deviceId);
                                            CTX.startService(background1);

                                        }


                                        break;

                                    case "Cancel":

                                        String errorMessage = info.getOutputData().getString(CreateSessionWSJob.KEY_CANCEL_MESSAGE);

                                        break;


                                }

                            }


                        }
                    }
                });

    }



}
