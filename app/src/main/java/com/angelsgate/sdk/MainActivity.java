package com.angelsgate.sdk;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.angelsgate.sdk.AngelsGateDownload.DownloadActivity;
import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.AngelsGateNetwork.model.TestDataRequest;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnCancelListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnProgressListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnStartListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnUploadFailedListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnUploadListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners.OnWaitListener;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.OMUploader;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.Progress;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request.UploadRequest;
import com.angelsgate.sdk.AngelsGateUtils.AngelGateConstants;
import com.angelsgate.sdk.AngelsGateUtils.BitmapUtils;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;
import com.angelsgate.sdk.AngelsGateUtils.fileUtils;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.JobManager;
import com.angelsgate.sdk.AngelsGateUtils.prefs.AngelGatePreferencesHelper;
import com.angelsgate.sdk.AngelsGateWebsocket.WebSocketActivity;
import com.angelsgate.sdk.jobs.AbortFileJob;
import com.angelsgate.sdk.jobs.DeleteFileJob;
import com.angelsgate.sdk.jobs.ForwardFileJob;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ApiInterface apiInterface;
    JobManager jobmanager;
    String selectedPath = "";
    public static int ChooseFilerequestCode = 1345;
    public static int ReadExPermisionrequestCode = 1345;

    public static String deviceId;
    private static final String DIGEST_ALGORITHM_SHA1 = "SHA1";
    public static ApiInterface apiInterface2;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ////////////////
        Button preAuth_button = (Button) findViewById(R.id.preAuth);
        Button postAuth_button = (Button) findViewById(R.id.postAuth);
        Button test_button = (Button) findViewById(R.id.test);
        Button signal_button = (Button) findViewById(R.id.signal);
        Button select_file_button = (Button) findViewById(R.id.select_file);
        Button downloadfile_button = (Button) findViewById(R.id.downloadfile);
        Button startWebsocket = (Button) findViewById(R.id.start);
        Button setproxy = (Button) findViewById(R.id.setproxy);


        deviceId = "123456";
        GETApiInterface();

        preAuth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preAuth(deviceId);
            }
        });


        postAuth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAuth(deviceId);
            }
        });


        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test(deviceId);


            }
        });

        signal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signal(deviceId);
            }
        });


        select_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPath = "";
                checkPermissionsAndOpenFilePicker();


            }
        });

        downloadfile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadFile();

            }
        });


        startWebsocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, WebSocketActivity.class);
                startActivity(i);
                finish();
            }
        });


        setproxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GETApiInterfacebyProxy("", 8080, Proxy.Type.HTTP);
            }
        });

         //test url
        String baseUrl = "";
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new EncodeRequestInterceptor(getApplicationContext()))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")

                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        apiInterface = retrofit.create(ApiInterface.class);
        jobmanager = new JobManager(getApplicationContext(), WorkManager.getInstance());

        //////////////
        //Get From Server
        String iv = "";
        //Get From Server
        String SecretKey = "";
        //Get From Server
        String PublicKey = "";


        AngelGateConstants angel = new AngelGateConstants.AngelGateConstantsBuilder(
                PublicKey, iv, SecretKey, "PreAuth", "PostAuth", baseUrl)
                .setMaxLengthSsalt(12)
                .setMintLengthSsalt(8)
                .build();


        /////////////////
        ////RequestHeader
//        final long segment = AngelsGate.CreatSegment(MainActivity.this);
//        final String Ssalt = AngelsGate.CreatSsalt();
//        final long TimeStamp = AngelsGate.CreatTimeStamp();
//        final String Request = "Test";
//        boolean isArrayRequest = false;
//        final String DeviceId = "123456";


//        TestDataRequest input = new TestDataRequest("HELLO");
//        try {
//            Response<ResponseBody> response = apiInterface.TestApi(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest,input).execute();
//            if (response.isSuccessful()) {
//                String bodyResponse = response.body().string();
//                String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, MainActivity.this);
//                AngelsGate.ErroreHandler(data_response);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////RequestHeader
//        final long segment = AngelsGate.CreatSegment(MainActivity.this);
//        final String Ssalt = AngelsGate.CreatSsalt();
//        final long TimeStamp = AngelsGate.CreatTimeStamp();
//        final String Request = "Test";
//        boolean isArrayRequest = false;
//        final String DeviceId = "123456";
//
//
//        OkHttpClient client = new OkHttpClient();
//
//        //add parameters
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.example.com").newBuilder();
//        urlBuilder.addQueryParameter("query", "example");
//
//
//        String url = urlBuilder.build().toString();
//
//        //build the request
//        Request request = new Request.Builder().url(url).build();
//
//
//        try {
//            request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//
//
//        //execute
//        try {
//            okhttp3.Response response2 = client.newCall(request).execute();
//
//            String bodyResponse = response2.body().string();
//            String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, MainActivity.this);
//            AngelsGate.ErroreHandler(data_response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//


//        String s = "ALIomidNIMA";
//        byte[] data = null;
//        try {
//            data = s.getBytes("UTF8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("test data " + data.length);
//
//
//        byte[] res1 = EncodeAlgorithmUtils.Deflate(data);
//
//
//        System.out.println("test result " + Base64Utils.Base64Encode(res1));


//        String ss = "eNrLz81MSczJzMvMTQQAG00EhQ==";
//
//
//
//        byte[] res2 = EncodeAlgorithmUtils.Inflate(Base64Utils.Base64DecodeToByte(ss));
//
//
//        String s2 = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            s2 = new String(res2, StandardCharsets.UTF_8);
//        }
//
//        System.out.println("test result " + s2);


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);


        }


    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, ReadExPermisionrequestCode);
            }
        } else {
            openFilePicker();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1345: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    private void showError() {
        Toast.makeText(this, "بدون دسترسی ، امکان اجرای برنامه وجود ندارد", Toast.LENGTH_LONG).show();

    }


    private void openFilePicker() {

        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(ChooseFilerequestCode)
                .withHiddenFiles(false) // Show hidden files and folders
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ChooseFilerequestCode && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (path != null) {

                selectedPath = path;
                Toast.makeText(this, " فایل انتخاب شد", Toast.LENGTH_LONG).show();

                validation();

            }
        }
    }


    private void validation() {
        if (selectedPath != null && !selectedPath.equals("")) {


            new checksumTask().execute();

        } else {

            Toast.makeText(this, "فایل را انتخاب کنید", Toast.LENGTH_LONG).show();


        }
    }

    class checksumTask extends AsyncTask<Void, Void, Result> {

        @Override
        protected void onPreExecute() {

        }


        protected Result doInBackground(Void... inputData) {


            String filename = FilenameUtils.getName(selectedPath);
            String extension = FilenameUtils.getExtension(selectedPath);

            InputStream is = null;
            try {
                is = new FileInputStream(selectedPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            File file = new File(selectedPath);



            //create checksum file
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ae) {

            }


            byte[] buffer = new byte[2048];
            int read = 0;
            try {
                while (true) {
                    try {
                        if (!((read = is.read(buffer)) > 0)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    digest.update(buffer, 0, read);
                }
            } catch (Exception e) {
            }


            //Create  the file hash
            byte[] digestBytes = digest.digest();

            String fileCheckSum = EncodeAlgorithmUtils.bytesToHex(digestBytes);


            if (fileCheckSum.length() > 32) {
                fileCheckSum = fileCheckSum.substring(0, 32);
            }





            String thumbString = "";
            try {
                Bitmap thumb = fileUtils.createThumbnailFromPath(selectedPath, MediaStore.Images.Thumbnails.MINI_KIND);
                thumbString = BitmapUtils.encodeTobase64(thumb);
            } catch (Exception e) {
                thumbString = "";
            }



            return new Result(true, fileCheckSum, filename, file.length(), extension, thumbString);

        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);

            if (result != null && result.Status == true) {



                startUpload(result.filename, result.filelength, result.getExtension(), result.getFileCheckSum(), result.getThumbString(), deviceId, selectedPath);


            } else {

            }

        }


    }

    ///UPLOAD
    private Runnable myUploadTask = new Runnable() {

        public void run() {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new EncodeRequestInterceptor(getApplicationContext()))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();


            OMUploader.initialize(getApplicationContext());

            request = OMUploader.getUploadRequestById(UploadId);

        }
    };


    String UploadId;
    UploadRequest request;

    public void startUpload(
            final String realname, final long filesize, final String extention, final String checksum, final String thumb, final String deviceId, final String selectedPath) {


        UploadId = com.angelsgate.sdk.AngelsGateUpload.Uploader.Utils.Utils.getUniqueId(selectedPath, realname);

        Thread backgroundThread = new Thread(myUploadTask);

        backgroundThread.start();

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getStatusUploadFile(
                        realname, filesize, extention, checksum, thumb, deviceId, selectedPath);
            }
        }, 3000);


    }


    public void getStatusUploadFile(
            final String realname, long filesize, String extention, String checksum, String thumb, final String deviceId, String selectedPath) {


        if (request != null) {

            request.setOnStartListener(new OnStartListener() {
                @Override
                public void onStart() {

                    System.out.println("test" + " onStartOrResume");

                }
            })

                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                            System.out.println("test" + " onCancel");

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                            System.out.println("test" + " onProgress " + progress.currentBytes);

                        }
                    })
                    .setOnUploadFailedListener(new OnUploadFailedListener() {
                        @Override
                        public void onUploadFailed(UploadException e) {
                            System.out.println("test" + " onDownloadFailed ");
                        }
                    })
                    .setOnWaitListener(new OnWaitListener() {
                        @Override
                        public void onWaited() {
                            System.out.println("test" + " onWaited ");
                        }
                    })
                    .setOnUploadListener(new OnUploadListener() {
                        @Override
                        public void onUploadComplete() {
                            System.out.println("test" + " onDownloadComplete ");
                        }
                    });


            System.out.println("test getStatusUploadFile " + request.getStatus());

            switch (request.getStatus()) {


                case 6:
                case 7:

                    uploadFileFromStart(realname, filesize, extention, checksum, thumb, deviceId, selectedPath);
                    break;

                case 1:
                case 2:
                case 0:
                    OMUploader.cancel(UploadId, MainActivity.this, request);
                    break;

                case 4:
                case 5:
                    OMUploader.cancel(UploadId, MainActivity.this, request);
                    break;
            }


        } else {

            uploadFileFromStart(realname, filesize, extention, checksum, thumb, deviceId, selectedPath);
        }


    }


    public void uploadFileFromStart(final String realname, long filesize, String extention, String checksum, String thumb, final String deviceId, String selectedPath) {


        String uploadId = OMUploader.upload(realname, filesize, extention, checksum, thumb, deviceId, selectedPath)
                .build()
                .setOnStartListener(new OnStartListener() {
                    @Override
                    public void onStart() {

                        System.out.println("test" + " onStart");

                    }
                })

                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                        System.out.println("test" + " onCancel");

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        System.out.println("test" + " onProgress" + progress.currentBytes);

                        //update notification
                    }
                })
                .setOnUploadFailedListener(new OnUploadFailedListener() {
                    @Override
                    public void onUploadFailed(UploadException e) {


                        System.out.println("test" + " onDownloadFailed" + e.getCode());
                        System.out.println("test" + " onDownloadFailed" + e.getMessage());


                    }
                })
                .setOnWaitListener(new OnWaitListener() {
                    @Override
                    public void onWaited() {
                        System.out.println("test" + " onWaited");
                    }
                })
                .startWithListner(new OnUploadListener() {


                    @Override
                    public void onUploadComplete() {

                        System.out.println("test" + " onDownloadComplete");

                    }


                }, getApplicationContext());


    }


    class Result {
        boolean Status;
        String fileCheckSum;

        String filename;
        long filelength;
        String extension;
        String thumbString;

        public Result(boolean status, String fileCheckSum, String filename, long filelength, String extension, String thumbString) {
            Status = status;
            this.fileCheckSum = fileCheckSum;

            this.filename = filename;
            this.filelength = filelength;
            this.extension = extension;
            this.thumbString = thumbString;
        }

        public boolean isStatus() {
            return Status;
        }

        public void setStatus(boolean status) {
            Status = status;
        }

        public String getFileCheckSum() {
            return fileCheckSum;
        }

        public void setFileCheckSum(String fileCheckSum) {
            this.fileCheckSum = fileCheckSum;
        }


        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public long getFilelength() {
            return filelength;
        }

        public void setFilelength(long filelength) {
            this.filelength = filelength;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getThumbString() {
            return thumbString;
        }

        public void setThumbString(String thumbString) {
            this.thumbString = thumbString;
        }
    }



    public void preAuth(String deviceId) {

        AngelGatePreferencesHelper.ResetAllData(MainActivity.this);
        ///PreAuth
        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(MainActivity.this);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "PreAuth";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;


        Call<ResponseBody> callback = apiInterface.PreAuth(TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest);
        callback.enqueue(new Callback<ResponseBody>() {


            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.isSuccessful()) {

                    System.out.println("preAuth suc");
                    String bodyResponse = null;
                    try {
                        bodyResponse = response.body().string();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {

                        System.out.println("preAuth " + bodyResponse);
                        if (AngelsGate.StringErroreHandler(bodyResponse)) {

                            String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt, DeviceId, Request, MainActivity.this);

                            System.out.println("data_response" + data_response);

                            AngelsGate.ErroreHandler(data_response);

                        } else {


                        }


                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }


                } else {
                    System.out.println("preAuth error");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("preAuth error222");
            }
        });


    }

    public void postAuth(String deviceId) {

        ////PostAuth
        ////RequestHeader
        final int segment2 = AngelsGate.CreatSegment(MainActivity.this);
        final String Ssalt2 = AngelsGate.CreatSsalt();
        final long TimeStamp2 = AngelsGate.CreatTimeStamp();
        final String Request2 = "PostAuth";
        boolean isArrayRequest2 = false;
        final String DeviceId2 = deviceId;


        Call<ResponseBody> callback2 = apiInterface.PostAuth(TimeStamp2, DeviceId2, segment2, Ssalt2, Request2, isArrayRequest2);
        callback2.enqueue(new Callback<ResponseBody>() {


            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    String bodyResponse = null;
                    try {
                        bodyResponse = response.body().string();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {


                        if (AngelsGate.StringErroreHandler(bodyResponse)) {

                            String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt2, DeviceId2, Request2, MainActivity.this);

                            System.out.println("data_response" + data_response);
                            AngelsGate.ErroreHandler(data_response);

                        } else {


                        }


                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }


                } else {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void test(String deviceId) {
        ///TestApi
        ////RequestHeader
        final int segment3 = AngelsGate.CreatSegment(MainActivity.this);
        final String Ssalt3 = AngelsGate.CreatSsalt();
        final long TimeStamp3 = AngelsGate.CreatTimeStamp();
        final String Request3 = "checkUpdate";
        boolean isArrayRequest3 = false;
        final String DeviceId3 = deviceId;

        TestDataRequest input = new TestDataRequest("hello");

        Call<ResponseBody> callback3 = apiInterface.TestApi(TimeStamp3, DeviceId3, segment3, Ssalt3, Request3, isArrayRequest3, input);
        callback3.enqueue(new Callback<ResponseBody>() {


            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.isSuccessful()) {

                    String bodyResponse = null;
                    try {
                        bodyResponse = response.body().string();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {


                        if (AngelsGate.StringErroreHandler(bodyResponse)) {

                            String data_response = AngelsGate.DecodeResponse(bodyResponse, Ssalt3, DeviceId3, Request3, MainActivity.this);

                            System.out.println("data_response" + data_response);


                            AngelsGate.ErroreHandler(data_response);

                        } else {


                        }


                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }


                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    public void signal(String deviceId) {
        ///signal
        ////RequestHeader
        final int segment4 = AngelsGate.CreatSegment(MainActivity.this);
        final String Ssalt4 = AngelsGate.CreatSsalt();
        final long TimeStamp4 = AngelsGate.CreatTimeStamp();
        final String Request4 = AngelGateConstants.SignalMethodName;
        boolean isArrayRequest4 = false;
        final String DeviceId4 = deviceId;

        TestDataRequest input = new TestDataRequest("HELLO");


        Call<ResponseBody> callback3 = apiInterface.signal(TimeStamp4, DeviceId4, segment4, Ssalt4, Request4, isArrayRequest4, input);
        callback3.enqueue(new Callback<ResponseBody>() {


            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {


                    String bodyResponse = null;
                    try {
                        bodyResponse = response.body().string();

                        System.out.println("data_response" + bodyResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    boolean error = AngelsGate.ErroreHandler(bodyResponse);


                    if (!error) {
                        ///ERROR IN RESPONSE
                    } else {

                        if (Integer.parseInt(bodyResponse) > 0) {

                            //ACTION
                        } else {
                            String SignalError = AngelsGate.SignalErroreHandler(Integer.parseInt(bodyResponse));

                        }

                    }


                } else {


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    public void AbortFile(String handler, String deviceId) {

        Data.Builder dataBuilder = AbortFileJob.constructData(handler, deviceId);

        OneTimeWorkRequest Jobrequest = jobmanager.add(AbortFileJob.class, AbortFileJob.constructParameters(), dataBuilder);


        WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                .observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished()) {
                            String StatusResult = info.getOutputData().getString(AbortFileJob.KEY_RESULT);

                            if (StatusResult != null) {
                                System.out.println("testJob1 " + "StatusResult " + StatusResult);

                                switch (StatusResult) {

                                    case "success":
                                        String responseString = info.getOutputData().getString(AbortFileJob.KEY_RESPONSE);
                                        System.out.println("test responseString" + "responseString " + responseString);

                                        switch (responseString) {
                                            case "NOTICE_FILE_ABORT":
                                                break;

                                            case "NOTICE_FILE_CANNOTABORT":
                                                break;


                                        }


                                        break;
                                    case "Cancel":

                                        String errorMessage = info.getOutputData().getString(AbortFileJob.KEY_CANCEL_MESSAGE);

                                        break;


                                }

                            }


                        }
                    }
                });


    }



    public void DeleteFile(String handler, String deviceId) {


        Data.Builder dataBuilder = DeleteFileJob.constructData(handler, deviceId);

        OneTimeWorkRequest Jobrequest = jobmanager.add(DeleteFileJob.class, DeleteFileJob.constructParameters(), dataBuilder);


        WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                .observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished()) {
                            String StatusResult = info.getOutputData().getString(DeleteFileJob.KEY_RESULT);

                            if (StatusResult != null) {
                                System.out.println("testJob1 " + "StatusResult " + StatusResult);

                                switch (StatusResult) {

                                    case "success":
                                        String itemsJson = info.getOutputData().getString(DeleteFileJob.KEY_RESPONSE);

                                        break;
                                    case "Cancel":

                                        String errorMessage = info.getOutputData().getString(DeleteFileJob.KEY_CANCEL_MESSAGE);

                                        break;


                                }

                            }


                        }
                    }
                });
    }


    public void ForwarfFile(String handler, String peer, String deviceId) {


        Data.Builder dataBuilder = ForwardFileJob.constructData(handler, peer, deviceId);

        OneTimeWorkRequest Jobrequest = jobmanager.add(ForwardFileJob.class, ForwardFileJob.constructParameters(), dataBuilder);


        WorkManager.getInstance().getWorkInfoByIdLiveData(Jobrequest.getId())
                .observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo info) {
                        if (info != null && info.getState().isFinished()) {
                            String StatusResult = info.getOutputData().getString(ForwardFileJob.KEY_RESULT);

                            if (StatusResult != null) {
                                System.out.println("testJob1 " + "StatusResult " + StatusResult);

                                switch (StatusResult) {

                                    case "success":
                                        String itemsJson = info.getOutputData().getString(ForwardFileJob.KEY_RESPONSE);

                                        break;
                                    case "Cancel":

                                        String errorMessage = info.getOutputData().getString(ForwardFileJob.KEY_CANCEL_MESSAGE);

                                        break;


                                }

                            }


                        }
                    }
                });
    }


    public ApiInterface GETApiInterface() {
        String baseUrl = "";
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new EncodeRequestInterceptor(getApplicationContext()))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        apiInterface2 = retrofit.create(ApiInterface.class);

        return apiInterface2;
    }


    public ApiInterface GETApiInterfacebyProxy(final String hostname, final int Port, Proxy.Type type) {

        java.net.Proxy proxy = new Proxy(type, new InetSocketAddress(hostname, Port));


        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestingHost().equalsIgnoreCase(hostname)) {
                    if (Port == getRequestingPort()) {
                        return new PasswordAuthentication("username", "password".toCharArray());
                    }
                }
                return null;
            }
        });


        final String username = "username";
        final String password = "password";

        okhttp3.Authenticator proxyAuthenticator = new okhttp3.Authenticator() {
            public Request authenticate(Route route, okhttp3.Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            }
        };


        String baseUrl = "";
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new EncodeRequestInterceptor(getApplicationContext()))
                .proxy(proxy)
                .proxyAuthenticator(proxyAuthenticator)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        apiInterface = retrofit.create(ApiInterface.class);

        return apiInterface;
    }


    public void downloadFile() {
        Intent i = new Intent(MainActivity.this, DownloadActivity.class);
        startActivity(i);
        finish();
    }


}
