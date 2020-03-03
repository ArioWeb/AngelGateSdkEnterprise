package com.angelsgate.sdk.jobs;

import android.content.Context;


import com.angelsgate.sdk.AngelsGateUtils.jobmanager.Job;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.requirements.ContextDependent;
import com.angelsgate.sdk.ApiInterface;

import androidx.work.WorkerParameters;


public abstract class ContextJob extends Job implements ContextDependent {



    protected transient Context context;


    public ContextJob(Context context, WorkerParameters workerParams ) {
        super(context, workerParams);
        this.context = context;

    }



    public void setContext(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }



}
