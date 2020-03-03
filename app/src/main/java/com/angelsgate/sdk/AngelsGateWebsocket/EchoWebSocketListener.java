package com.angelsgate.sdk.AngelsGateWebsocket;

import android.app.Activity;
import android.widget.TextView;

import com.angelsgate.sdk.AngelsGate;
import com.angelsgate.sdk.AngelsGateDB.AppDbHelper;
import com.angelsgate.sdk.AngelsGateDB.database.AppDatabase;
import com.angelsgate.sdk.AngelsGateDB.entity.SocketRequest;
import com.angelsgate.sdk.AngelsGateNetwork.DecodeResponseFromSocket;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.CheckFileEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.CheckUpdateEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.CreatSessionEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.LeftPartEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.PostAuthEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.PreAuthEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.PushPartEvent;
import com.angelsgate.sdk.AngelsGateWebsocket.Events.SignalEvent;

import org.greenrobot.eventbus.EventBus;

import java.security.GeneralSecurityException;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EchoWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;


    Activity ctx;
    TextView output;
    String DeviceId;

    public EchoWebSocketListener(Activity ctx, TextView output, String DeviceId) {
        this.ctx = ctx;
        this.output = output;
        this.DeviceId = DeviceId;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        output("Receiving : " + text, ctx, output);


        if (AngelsGate.StringErroreHandler(text)) {



            int Segment = 0;
            try {
                Segment = DecodeResponseFromSocket.FirstDecode(text);
            } catch (Exception e) {
                e.printStackTrace();

            }




            AppDbHelper databaseHelper = new AppDbHelper(ctx.getApplicationContext());

            SocketRequest request = databaseHelper.loadSocketRequest(Segment);



            String response = null;
            try {
                response = DecodeResponseFromSocket.decode(text, request.getSsalt(), DeviceId, request.getMethodName(), ctx);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();

             }


            switch (request.getMethodName()) {
                case "CheckFile":
                    EventBus.getDefault().post(new CheckFileEvent(true, response));
                    break;

                case "StoreFile":
                    EventBus.getDefault().post(new CreatSessionEvent(true, response));
                    break;


                case "LeftPart":
                    EventBus.getDefault().post(new LeftPartEvent(true, response));
                    break;


                case "PushPart":
                    EventBus.getDefault().post(new PushPartEvent(true, response));
                    break;


                case "PreAuth":
                    EventBus.getDefault().post(new PreAuthEvent(true, response));
                    break;


                case "PostAuth":
                    EventBus.getDefault().post(new PostAuthEvent(true, response));
                    break;


                case "checkUpdate":
                    EventBus.getDefault().post(new CheckUpdateEvent(true, response));
                    break;


                case "signal":
                    EventBus.getDefault().post(new SignalEvent(true, response));
                    break;

            }



        }


    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        output("Receiving bytes : " + bytes.hex(), ctx, output);


    }


    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        output("Closing : " + code + " / " + reason, ctx, output);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        output("Error : " + t.getMessage() + t.getCause()+t.getStackTrace().toString(), ctx, output);
    }

    private void output(final String txt, Activity act, final TextView output) {

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText( txt);
            }
        });

    }


}