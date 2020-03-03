package com.angelsgate.sdk.AngelsGateWebsocket.HeartBeat;

import android.content.Context;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateUtils.NetworkUtils;
import com.angelsgate.sdk.AngelsGateWebsocket.WebSocketActivity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import okhttp3.RequestBody;

public class heartBeatTask implements Runnable {

    Context ctx;
    String deviceId;

    public heartBeatTask(Context ctx, String deviceId) {
        this.ctx = ctx;
        this.deviceId = deviceId;
    }


    @Override
    public void run() {

        while (true) {
            ////RequestHeader
            final int segment = AngelsGate.CreatSegment(ctx);
            final String Ssalt = AngelsGate.CreatSsalt();
            final long TimeStamp = AngelsGate.CreatTimeStamp();
            final String Request = "heartbeat";
            boolean isArrayRequest = false;
            final String DeviceId = deviceId;


            okhttp3.Request request = NetworkUtils.CreateRequestForSocket("");


            try {
                request = AngelsGate.EncodeRequest(request, TimeStamp, DeviceId, segment, Ssalt, Request, isArrayRequest, ctx);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }



            RequestBody requestBody = request.body();
            String rawJson = NetworkUtils.bodyToString(requestBody);

            WebSocketActivity.sendMessage(rawJson);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }
}
