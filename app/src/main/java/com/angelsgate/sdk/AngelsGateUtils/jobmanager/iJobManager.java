package com.angelsgate.sdk.AngelsGateUtils.jobmanager;


import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;

public interface iJobManager {

    OneTimeWorkRequest add(Class<? extends Worker> jobclass, JobParameters jobParameters, Data.Builder dataBuilder);
}
