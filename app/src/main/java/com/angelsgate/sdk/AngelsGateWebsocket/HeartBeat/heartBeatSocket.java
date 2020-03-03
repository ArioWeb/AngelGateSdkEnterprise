package com.angelsgate.sdk.AngelsGateWebsocket.HeartBeat;

import com.angelsgate.sdk.AngelsGateWebsocket.HeartBeat.heartBeatStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class heartBeatSocket extends FutureTask<heartBeatStatus> {


    public heartBeatSocket(Runnable runnable, heartBeatStatus result) {
        super(runnable, result);
    }

    public heartBeatSocket(Callable<heartBeatStatus> callable) {
        super(callable);
    }


}
