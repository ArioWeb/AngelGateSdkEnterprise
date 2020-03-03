package com.angelsgate.sdk.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadPart;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.FileUploadSessionPartRequest;
import com.angelsgate.sdk.AngelsGateNetwork.model.file.SessionResponse;
import com.angelsgate.sdk.AngelsGateUtils.AngelGateConstants;
import com.angelsgate.sdk.AngelsGateUtils.Base64Utils;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;

import com.angelsgate.sdk.AngelsGateUtils.WebSocketUp.CheckFileWSTask;
import com.angelsgate.sdk.AngelsGateUtils.WebSocketUp.LargeFileUploadWSTask;
import com.angelsgate.sdk.AngelsGateUtils.WebSocketUp.LeftPartWSTask;
import com.angelsgate.sdk.MainActivity;
import com.angelsgate.sdk.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class uploadHelperWSService extends Service {


    private static final String TAG = uploadHelperWSService.class.getSimpleName();

    public static int FOREGROUND_SERVICE_NOTIFICATION_ID = 103234;


    private boolean isRunning = false;
    private Thread backgroundThread;


    private static final int THREAD_POOL_WAIT_TIME_IN_MILLIS = 5000;
    private ThreadPoolExecutor executorService;
    private ThreadPoolExecutor checkFileExecutorService;
    private ThreadPoolExecutor LeftPartExecutorService;
    private long timeout;
    private TimeUnit timeUnit;
    private static final int DEFAULT_CONNECTIONS = 1;
    private static final int DEFAULT_TIMEOUT = 1;
    private static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.HOURS;

    //////////////////
    static SessionResponse session;
    static InputStream stream;
    static long fileSize;
    static String deviceId;
    static int partSize;
    static int partSize2;
    static String selectedPath;
    ///////////////////
    public static String CheckFileResponse = "";
    public static String LeftPartResponse = "";


    public static Intent getStartIntent(Context context) {
        return new Intent(context, uploadHelperWSService.class);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, uploadHelperWSService.class);
        context.startService(starter);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, uploadHelperWSService.class));
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        this.isRunning = false;
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(uploadHelperWSService.DEFAULT_CONNECTIONS);
        this.checkFileExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.LeftPartExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.timeout = uploadHelperWSService.DEFAULT_TIMEOUT;
        this.timeUnit = uploadHelperWSService.DEFAULT_TIMEUNIT;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {

            try {
                uploadHelper(session, stream, fileSize);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopForeground(true);
                stopSelf();
            } catch (IOException e) {
                e.printStackTrace();
                stopForeground(true);
                stopSelf();
            }

            stopForeground(true);
            stopSelf();
        }
    };


    public static SessionResponse getSession() {
        return session;
    }

    public static void setSession(SessionResponse sessionitem) {

        session = sessionitem;
        partSize = session.getPartsize();
        partSize2= session.getPartsize();

    }

    public static InputStream getStream() {
        return stream;
    }

    public static void setStream(InputStream streamitem) {
        stream = streamitem;
    }

    public static long getFileSize() {
        return fileSize;
    }

    public static void setFileSize(long fileSizeitem) {
        fileSize = fileSizeitem;
    }


    public static String getdeviceId() {
        return deviceId;
    }

    public static void setdeviceId(String mydeviceId) {
        deviceId = mydeviceId;
    }

    public static String getSelectedPath() {
        return selectedPath;
    }

    public static void setSelectedPath(String selectedPath) {
        uploadHelperWSService.selectedPath = selectedPath;
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;

        session = null;
        stream = null;
        fileSize = 0;
        deviceId = null;
        System.gc();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {

            if (!this.isRunning) {
                this.isRunning = true;
                startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID,
                        showNotification());
                this.backgroundThread.start();

            }


        } catch (Exception e) {

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }


    public void uploadHelper(SessionResponse session, InputStream stream, long fileSize)
            throws InterruptedException, IOException {

        Thread.sleep(5000);

        List<FileUploadSessionPartRequest> parts = this.uploadParts(session, stream, fileSize);



        Thread.sleep(10000);

        CheckFileResponse = "";
        checkFileExecutorService.execute(
                new CheckFileWSTask(getApplicationContext(), session.getHandler(), deviceId)
        );

        this.checkFileExecutorService.shutdown();
        this.checkFileExecutorService.awaitTermination(this.timeout, this.timeUnit);

        System.out.println("test end CheckFileTask  " + CheckFileResponse);

        if (!CheckFileResponse.isEmpty()) {

            switch (CheckFileResponse) {
                case "NOTICE_FILE_COMPLETE":
                    break;

                case "NOTICE_FILE_ABORT":
                    break;

                case "NOTICE_FILE_EXPIRE":
                    break;


                case "NOTICE_FILE_PENDING":




                    LeftPartResponse = "";
                    LeftPartExecutorService.execute(
                            new LeftPartWSTask(getApplicationContext(), session.getHandler(), deviceId)
                    );

                    this.LeftPartExecutorService.shutdown();
                    this.LeftPartExecutorService.awaitTermination(this.timeout, this.timeUnit);




                    if (LeftPartResponse.equals("NOTICE_FILE_PARTSEMPTY")) {

                        uploadHelper(session, stream, fileSize);

                    } else {

                        ArrayList<Integer> partsNotComplete = new ArrayList<>();
                        String substring = LeftPartResponse.substring(1, LeftPartResponse.length() - 1);
                        String[] partNumStrings = substring.split(",");

                        for (int i = 0; i < partNumStrings.length; i++) {
                            partsNotComplete.add(Integer.parseInt(partNumStrings[i]));
                            System.out.println("test left part " + Integer.parseInt(partNumStrings[i]));
                        }

                        if (partsNotComplete != null && partsNotComplete.size() > 0) {
                            this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(uploadHelperWSService.DEFAULT_CONNECTIONS);
                            uploadOnePart(session, stream, fileSize, partsNotComplete);
                        }



                    }


                    break;


                case "NOTICE_FILE_OTHER":
                    break;
            }


        }


        System.out.println("test end uploadHelper");

    }


    /*
     * Upload parts of the file. The part size is retrieved from the upload session.
     */
    public List<FileUploadSessionPartRequest> uploadParts(SessionResponse session, InputStream stream,
                                                          long fileSize) throws InterruptedException {


        List<FileUploadSessionPartRequest> parts = new ArrayList<FileUploadSessionPartRequest>();


        long offset = 0;
        long processed = 0;
        int partPostion = 0;


        //Set the Max Queue Size to 1.5x the number of processors
        double maxQueueSizeDouble = Math.ceil(this.executorService.getMaximumPoolSize() * 1.5);


        int maxQueueSize = Double.valueOf(maxQueueSizeDouble).intValue();


        while (processed < fileSize) {


            System.out.println("test1 fileSize " + fileSize);
            System.out.println("test1 processed " + processed);


            //Waiting for any thread to finish before
            long timeoutForWaitingInMillis = TimeUnit.MILLISECONDS.convert(this.timeout, this.timeUnit);


            if (this.executorService.getCorePoolSize() <= this.executorService.getActiveCount()) {

                if (timeoutForWaitingInMillis > 0) {
                    Thread.sleep(uploadHelperWSService.THREAD_POOL_WAIT_TIME_IN_MILLIS);
                    timeoutForWaitingInMillis -= THREAD_POOL_WAIT_TIME_IN_MILLIS;
                } else {

                }


            }


            if (this.executorService.getQueue().size() < maxQueueSize) {

                long diff = fileSize - (long) processed;
                //The size last part of the file can be lesser than the part size.
                if (diff < (long) partSize) {
                    partSize = (int) diff;
                }


                parts.add(null);

                byte[] bytes = new byte[partSize];
                try {
                    int readStatus = stream.read(bytes);
                    if (readStatus == -1) {
                        System.out.println("test  throw Stream ended while upload was progressing");
                    }
                } catch (IOException ioe) {

                    System.out.println("test  throw Reading data from stream failed.");
                }


                byte[] result = EncodeAlgorithmUtils.Deflate(bytes);
                String dataBase64 = Base64Utils.Base64Encode(result);


                AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
                databaseHelper.insertUploadPart(new UploadPart(session.getHandler(), partPostion, dataBase64));

                Thread.sleep(1000);


                if (!(executorService.isShutdown() || executorService.isTerminated())) {

                    this.executorService.execute(
                            new LargeFileUploadWSTask(getApplicationContext(), session.getHandler(),
                                    fileSize, parts, partPostion, deviceId, executorService)
                    );


                    //Increase the offset and proceesed bytes to calculate the Content-Range header.
                    processed += partSize;
                    offset += partSize;
                    partPostion++;
                } else {
                    break;
                }


            }


        }


        this.executorService.shutdown();
        this.executorService.awaitTermination(this.timeout, this.timeUnit);


        return parts;
    }


    private Notification showNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        //notificationIntent.setAction(AppConstants.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this, AngelGateConstants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("AngelGate")
                .setTicker("در حال آپلود")
                .setContentText("در حال آپلود")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();

        return notification;

    }

    public List<FileUploadSessionPartRequest> uploadOnePart(SessionResponse session, InputStream stream,
                                                            long fileSize, ArrayList<Integer> parts) throws InterruptedException {


        int a = (int) (fileSize / partSize2);

        int b = (int) (fileSize % partSize2);


        int lastpartNumber = -1;
        int lastpartSize = 0;


        if (b != 0) {
            lastpartNumber = a++;
            lastpartSize = b;
        } else {
            lastpartNumber = a;
            lastpartSize = partSize2;
        }

        List<FileUploadSessionPartRequest> sendparts = new ArrayList<FileUploadSessionPartRequest>();


        int offset = 0;
        int partPostion = 0;


        //Set the Max Queue Size to 1.5x the number of processors
        double maxQueueSizeDouble = Math.ceil(this.executorService.getMaximumPoolSize() * 1.5);


        int maxQueueSize = Double.valueOf(maxQueueSizeDouble).intValue();


        for (int i = 0; i < parts.size(); i++) {


            //Waiting for any thread to finish before
            long timeoutForWaitingInMillis = TimeUnit.MILLISECONDS.convert(this.timeout, this.timeUnit);


            if (this.executorService.getCorePoolSize() <= this.executorService.getActiveCount()) {

                if (timeoutForWaitingInMillis > 0) {
                    Thread.sleep(uploadHelperWSService.THREAD_POOL_WAIT_TIME_IN_MILLIS);
                    timeoutForWaitingInMillis -= THREAD_POOL_WAIT_TIME_IN_MILLIS;
                } else {
                    // throw new BoxAPIException("Upload parts timedout");
                }


            }


            if (this.executorService.getQueue().size() < maxQueueSize) {


                if (parts.get(i) != lastpartNumber) {



                    offset = (parts.get(i) - 1) * partSize2;
                    partPostion = (parts.get(i) - 1);



                    sendparts.add(null);

                    RandomAccessFile randomAccessfile = null;

                    try {
                        randomAccessfile = new RandomAccessFile(new File(getSelectedPath()), "rw");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                    byte[] bytes = new byte[partSize2];


                    try {



                        randomAccessfile.seek(offset);
                        int readStatus = randomAccessfile.read(bytes);


                         if (readStatus == -1) {

                            System.out.println("test  throw Stream ended while upload was progressing");
                        }
                    } catch (IOException ioe) {

                        System.out.println("test  throw Reading data from stream failed.");
                    }


                    byte[] result = EncodeAlgorithmUtils.Deflate(bytes);
                    String dataBase64 = Base64Utils.Base64Encode(result);


                    AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
                    databaseHelper.insertUploadPart(new UploadPart(session.getHandler(), partPostion, dataBase64));

                    Thread.sleep(300);


                    if (!(executorService.isShutdown() || executorService.isTerminated())) {

                        this.executorService.execute(
                                new LargeFileUploadWSTask(getApplicationContext(), session.getHandler(),
                                        fileSize, sendparts, partPostion, deviceId, executorService)
                        );

                    } else {
                        break;
                    }


                } else {




                    offset = (parts.get(i) - 1) * lastpartSize;
                    partPostion = (parts.get(i) - 1);




                    sendparts.add(null);
                    RandomAccessFile randomAccessfile = null;

                    try {
                        randomAccessfile = new RandomAccessFile(new File(getSelectedPath()), "rw");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }



                    byte[] bytes = new byte[lastpartSize];
                    try {

                        randomAccessfile.seek(offset);
                        int readStatus = randomAccessfile.read(bytes);



                        if (readStatus == -1) {

                            System.out.println("test  throw Stream ended while upload was progressing");
                        }
                    } catch (IOException ioe) {

                        System.out.println("test  throw Reading data from stream failed.");
                    }


                    byte[] result = EncodeAlgorithmUtils.Deflate(bytes);
                    String dataBase64 = Base64Utils.Base64Encode(result);


                    AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
                    databaseHelper.insertUploadPart(new UploadPart(session.getHandler(), partPostion, dataBase64));

                    Thread.sleep(300);


                    if (!(executorService.isShutdown() || executorService.isTerminated())) {

                        this.executorService.execute(
                                new LargeFileUploadWSTask(getApplicationContext(), session.getHandler(),
                                        fileSize, sendparts, partPostion, deviceId, executorService)
                        );

                    } else {
                        break;
                    }


                }

            }


        }


        this.executorService.shutdown();
        this.executorService.awaitTermination(this.timeout, this.timeUnit);

        return sendparts;

    }

}
