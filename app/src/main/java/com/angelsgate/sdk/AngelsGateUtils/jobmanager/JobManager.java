package com.angelsgate.sdk.AngelsGateUtils.jobmanager;


import android.content.Context;



import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

public class JobManager implements iJobManager {

    private static final Constraints NETWORK_CONSTRAINT = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final Context context;
    private final WorkManager workManager;

    OneTimeWorkRequest request;

    public JobManager(Context context, WorkManager workManager) {
        this.context = context;
        this.workManager = workManager;
    }

    public OneTimeWorkRequest add(final Class<? extends Worker> jobclass, final JobParameters jobParameters, final Data.Builder dataBuilder) {

        System.out.println("testJob" +"JobManager add" );

//
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                workManager.pruneWork();

//        JobParameters jobParameters = job.getJobParameters();

                if (jobParameters == null) {
                      throw new IllegalStateException("Jobs must have JobParameters at this stage. (" + jobclass.getName() + ")");
                }

                dataBuilder.putInt(Job.KEY_RETRY_COUNT, jobParameters.getRetryCount())
                        .putLong(Job.KEY_RETRY_UNTIL, jobParameters.getRetryUntil())
                        .putLong(Job.KEY_SUBMIT_TIME, System.currentTimeMillis())
                        .putBoolean(Job.KEY_REQUIRES_NETWORK, jobParameters.requiresNetwork());

                Data data = dataBuilder.build();

                OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest.Builder(jobclass)
                        .setInputData(data)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS);

                if (jobParameters.requiresNetwork()) {
                    requestBuilder.setConstraints(NETWORK_CONSTRAINT);
                }

                request = requestBuilder.build();

                System.out.println("testJob " +"create request" +request.getId());


                // job.onSubmit(context, request.getId());


                String groupId = jobParameters.getGroupId();
                if (groupId != null) {
                    System.out.println("testJob " +"enqueue 111"  );


                    ExistingWorkPolicy policy = jobParameters.shouldIgnoreDuplicates() ? ExistingWorkPolicy.KEEP : ExistingWorkPolicy.APPEND;
                    workManager.beginUniqueWork(groupId, policy, request).enqueue();
                } else {
                    System.out.println("testJob " +"enqueue 22"  );
                    workManager.beginWith(request).enqueue();
                }


           // }
//        });

        return request;
    }
}
