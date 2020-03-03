package com.angelsgate.sdk.AngelsGateDownload;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.angelsgate.sdk.AngelsGateDownload.downloader.Config;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnCancelListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnDownloadFailedListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnDownloadListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnPauseListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnProgressListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnStartOrResumeListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Listners.OnWaitListener;
import com.angelsgate.sdk.AngelsGateDownload.downloader.OMDownloader;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Progress;
import com.angelsgate.sdk.AngelsGateDownload.downloader.Utils.Utils;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.network.request.DownloadRequest;
import com.angelsgate.sdk.AngelsGateNetwork.EncodeRequestInterceptor;
import com.angelsgate.sdk.AngelsGateUtils.StorageUtils;
import com.angelsgate.sdk.ApiInterface;
import com.angelsgate.sdk.R;

import java.io.File;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DownloadActivity extends AppCompatActivity {

    DownloadRequest request;
    String DownloadId;

    String DownloadDirectoryPath = File.separator + "OMUploader";
    String InternalPath;

    private Runnable myTask = new Runnable() {
        public void run() {
            ///in app Loader class
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new EncodeRequestInterceptor(getApplicationContext()))
                    .build();

            Config config = new Config.Builder(getApplicationContext())
                    .setHttpClient(httpClient)
                    .setApiInterface(getRetrofit(httpClient))
                    .build();

            OMDownloader.initialize(getApplicationContext(), config);

            String downloadDirectoryPath = InternalPath + DownloadDirectoryPath;


            DownloadId = Utils.getUniqueId("CsIu1vXJlGNJvvso0XiW6KTyooPi9FVv", downloadDirectoryPath, "test");

            request = OMDownloader.getDownloadRequestById(DownloadId);


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);


        Button status_button = (Button) findViewById(R.id.status);
        Button start_button = (Button) findViewById(R.id.start);
        Button pause_button = (Button) findViewById(R.id.pause);
        TextView status_txt = (TextView) findViewById(R.id.statustex);


        Thread backgroundThread = new Thread(myTask);

        backgroundThread.start();

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStatusFile("CsIu1vXJlGNJvvso0XiW6KTyooPi9FVv", "test", "123456");
            }
        });


        InternalPath = StorageUtils.getInternalFilesDirectory(getApplicationContext());

        File DownloadDirectory = new File(InternalPath + DownloadDirectoryPath);

        StorageUtils.createDirectory(DownloadDirectory);


    }

    public void getStatusFile(String handler, String filename, String deviceId) {


        if (request != null) {

            request.setOnStartOrResumeListener(new OnStartOrResumeListener() {
                @Override
                public void onStartOrResume() {

                    System.out.println("test" + " onStartOrResume");

                }
            })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                            System.out.println("test" + " onPause");

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
                    .setOnDownloadFailedListener(new OnDownloadFailedListener() {
                        @Override
                        public void onDownloadFailed(DownloadException e) {
                            System.out.println("test" + " onDownloadFailed ");
                        }
                    })
                    .setOnWaitListener(new OnWaitListener() {
                        @Override
                        public void onWaited() {
                            System.out.println("test" + " onWaited ");
                        }
                    })
                    .setOnDownloadListener(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            System.out.println("test" + " onDownloadComplete ");
                        }
                    });


            switch (request.getStatus()) {
                case 6:
                case 3:
                case 7:

                    OMDownloader.resume(DownloadId, DownloadActivity.this, request);
                    break;

                case 1:
                case 2:
                case 0:
                    OMDownloader.pause(DownloadId, DownloadActivity.this, request);
                    break;

                case 4:
                case 5:
                    OMDownloader.cancel(DownloadId, DownloadActivity.this, request);
                    break;
            }


        } else {

            downloadFileFromStart(handler, filename, deviceId);
        }


    }


    public ApiInterface getRetrofit(OkHttpClient okHttpClient) {

        String baseUrl = "";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        return apiInterface;
    }


    public void downloadFileFromStart(String handler, String filename, String deviceId) {

        String downloadDirectoryPath = InternalPath + DownloadDirectoryPath;

        String downloadId = OMDownloader.download(handler, deviceId, downloadDirectoryPath, filename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                        System.out.println("test" + " onStartOrResume");

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                        System.out.println("test" + " onPause");

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
                .setOnDownloadFailedListener(new OnDownloadFailedListener() {
                    @Override
                    public void onDownloadFailed(DownloadException e) {

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
                .startWithListner(new OnDownloadListener() {


                    @Override
                    public void onDownloadComplete() {

                        System.out.println("test" + " onDownloadComplete");

                    }


                }, getApplicationContext());


    }


}
