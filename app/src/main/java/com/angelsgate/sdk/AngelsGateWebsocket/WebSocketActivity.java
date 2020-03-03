package com.angelsgate.sdk.AngelsGateWebsocket;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateDB.entity.UploadSession;
import com.angelsgate.sdk.AngelsGateNetwork.model.TestDataRequest;
import com.angelsgate.sdk.AngelsGateUtils.AngelGateConstants;
import com.angelsgate.sdk.AngelsGateUtils.BitmapUtils;
import com.angelsgate.sdk.AngelsGateUtils.EncodeAlgorithmUtils;
import com.angelsgate.sdk.AngelsGateUtils.NetworkUtils;
import com.angelsgate.sdk.AngelsGateUtils.WebSocketUp.LargeFileUploadWSUtil;
import com.angelsgate.sdk.AngelsGateUtils.fileUtils;
import com.angelsgate.sdk.AngelsGateUtils.prefs.AngelGatePreferencesHelper;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.CheckUpdateEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.PostAuthEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.PreAuthEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.SignalEvent;
import com.angelsgate.sdk.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;

public class WebSocketActivity extends AppCompatActivity {


    public static int ChooseFilerequestCode = 1345;
    public static int ReadExPermisionrequestCode = 1345;
    public static String deviceId;
    String selectedPath = "";
    static TextView outputWebsocket;
    private OkHttpClient client;
    public static WebSocket ws;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);


        Button preAuth_button = (Button) findViewById(R.id.preAuth);
        Button postAuth_button = (Button) findViewById(R.id.postAuth);
        Button test_button = (Button) findViewById(R.id.test);
        Button signal_button = (Button) findViewById(R.id.signal);
        Button select_file_button = (Button) findViewById(R.id.select_file);
        outputWebsocket = (TextView) findViewById(R.id.output);

        deviceId = "123456";

        preAuth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preAuthSocket(deviceId);
            }
        });


        postAuth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAuthSocket(deviceId);
            }
        });


        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    testSocket(deviceId);




            }
        });

        signal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signalSocket(deviceId);
            }
        });


        select_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPath = "";
                checkPermissionsAndOpenFilePicker();


            }
        });


        CreateWebSocket();
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
        Toast.makeText(this, "بدون دسترسسی ، امکان اجرای برنامه وجود ندارد", Toast.LENGTH_LONG).show();

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


            AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
            databaseHelper.insertUploadSession(new UploadSession(filename + file.length(), thumbString));


            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return new Result(true, fileCheckSum, filename, file.length(), extension);

        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);

            if (result != null && result.Status == true) {


                InputStream is = null;
                try {
                    is = new FileInputStream(selectedPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                startUpload(is, result.filename, result.filelength, result.getExtension(), result.getFileCheckSum(), "", deviceId, selectedPath);


            } else {

            }

        }


    }


    public void startUpload(InputStream stream,
                            String realname, long filesize, String extention, String checksum, String thumb, String deviceId, String selectedPath) {


        LargeFileUploadWSUtil utils = new LargeFileUploadWSUtil(getApplicationContext());

        try {
            utils.uploadFile(stream,
                    realname, filesize, extention, checksum, thumb, deviceId, selectedPath);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    class Result {
        boolean Status;
        String fileCheckSum;

        String filename;
        long filelength;
        String extension;

        public Result(boolean status, String fileCheckSum, String filename, long filelength, String extension) {
            Status = status;
            this.fileCheckSum = fileCheckSum;

            this.filename = filename;
            this.filelength = filelength;
            this.extension = extension;
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
    }



    protected void CreateWebSocket() {
        client = new OkHttpClient.Builder()
                .build();
        Request Startrequest = new Request.Builder().url("").build();
        EchoWebSocketListener listener = new EchoWebSocketListener(this, outputWebsocket, deviceId);
        ws = client.newWebSocket(Startrequest, listener);


    }



    public void preAuthSocket(String deviceId) {

        AngelGatePreferencesHelper.ResetAllData(this);
        ///PreAuth
        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(this);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "PreAuth";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;


        Request request = NetworkUtils.CreateRequestForSocket("");

        try {
            request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = request.body();
        String rawJson = NetworkUtils.bodyToString(requestBody);
        new saveDataTask().execute((new inputData(segment, Ssalt, Request, rawJson)));
    }

    public void postAuthSocket(String deviceId) {
        ////PostAuth
        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(this);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "PostAuth";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;

        Request request = NetworkUtils.CreateRequestForSocket("");

        try {
            request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = request.body();
        String rawJson = NetworkUtils.bodyToString(requestBody);
        new saveDataTask().execute((new inputData(segment, Ssalt, Request, rawJson)));




    }


    public void testSocket(String deviceId) {
        ///TestApi
        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(this);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = "checkUpdate";
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;

        TestDataRequest input = new TestDataRequest("hello");

        String inputString = NetworkUtils.ConvertObjectToString(input);
        Request request = NetworkUtils.CreateRequestForSocket(inputString);

        try {
            request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = request.body();
        String rawJson = NetworkUtils.bodyToString(requestBody);

        new saveDataTask().execute((new inputData(segment, Ssalt, Request, rawJson)));
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }


    public void signalSocket(String deviceId) {
        ///signal
        ////RequestHeader
        final int segment = AngelsGate.CreatSegment(this);
        final String Ssalt = AngelsGate.CreatSsalt();
        final long TimeStamp = AngelsGate.CreatTimeStamp();
        final String Request = AngelGateConstants.SignalMethodName;
        boolean isArrayRequest = false;
        final String DeviceId = deviceId;

        TestDataRequest input = new TestDataRequest("HELLO");

        String inputString = NetworkUtils.ConvertObjectToString(input);
        Request request = NetworkUtils.CreateRequestForSocket(inputString);

        try {
            request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = request.body();
        String rawJson = NetworkUtils.bodyToString(requestBody);


        new saveDataTask().execute((new inputData(segment, Ssalt, Request, rawJson)));



    }


    public static void sendMessage(final String message) {

        ws.send(message);

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreAuthEvent(PreAuthEvent event) {

        boolean statusREesponse = event.isStatus();
        String bodyRespons = "";
        if (statusREesponse) {
            bodyRespons = event.getResponseBody();
        }
        AngelsGate.ErroreHandler(bodyRespons);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostAuthEvent(PostAuthEvent event) {

        boolean statusREesponse = event.isStatus();
        String bodyRespons = "";
        if (statusREesponse) {
            bodyRespons = event.getResponseBody();
        }
        AngelsGate.ErroreHandler(bodyRespons);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckUpdateEvent(CheckUpdateEvent event) {
        boolean statusREesponse = event.isStatus();
        String bodyRespons = "";
        if (statusREesponse) {
            bodyRespons = event.getResponseBody();
        }
        AngelsGate.ErroreHandler(bodyRespons);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalEvent(SignalEvent event) {

        boolean statusREesponse = event.isStatus();
        String bodyRespons = "";
        if (statusREesponse) {
            bodyRespons = event.getResponseBody();
        }

        boolean error = AngelsGate.ErroreHandler(bodyRespons);


        if (!error) {
            ///ERROR IN RESPONSE
        } else {

            if (Integer.parseInt(bodyRespons) > 0) {

                //ACTION
            } else {
                String SignalError = AngelsGate.SignalErroreHandler(Integer.parseInt(bodyRespons));

            }

        }


    }



    class saveDataTask extends AsyncTask<inputData, Void, Void> {

        @Override
        protected void onPreExecute() {

        }


        protected Void doInBackground(inputData... inputData) {


            AppDbHelper databaseHelper = new AppDbHelper(getApplicationContext());
            databaseHelper.insertSocketRequest(new SocketRequest(inputData[0].getSegment(), inputData[0].getSsalt(), inputData[0].getMethodName()));
            sendMessage(inputData[0].getRowJson());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }


    }





    public class inputData {


        public int segment;
        public String Ssalt;
        public String methodName;
        public String rowJson;

        public inputData(int segment, String ssalt, String methodName, String rowJson) {
            this.segment = segment;
            Ssalt = ssalt;
            this.methodName = methodName;
            this.rowJson = rowJson;
        }

        public int getSegment() {
            return segment;
        }

        public void setSegment(int segment) {
            this.segment = segment;
        }

        public String getSsalt() {
            return Ssalt;
        }

        public void setSsalt(String ssalt) {
            Ssalt = ssalt;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getRowJson() {
            return rowJson;
        }

        public void setRowJson(String rowJson) {
            this.rowJson = rowJson;
        }
    }
}
