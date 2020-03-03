

package com.angelsgate.sdk.AngelsGateUpload.Uploader.core;

import android.os.Process;


import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultExecutorSupplier implements ExecutorSupplier {

    private static final int DEFAULT_MAX_NUM_THREADS = ComponentHolder.getInstance().getUploadTasks();
    private static final int DEFAULT_MAX_NUM_THREADS_FOR_PARTS_UPLOAD = ComponentHolder.getInstance().getUploadThread();

    private final ThreadPoolExecutor networkExecutor;
    private final ThreadPoolExecutor backgroundExecutor;
    private final Executor mainThreadExecutor;
    private final ThreadPoolExecutor partUploadExecutor;

    DefaultExecutorSupplier() {
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        networkExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS);
        partUploadExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS_FOR_PARTS_UPLOAD);
        backgroundExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS_FOR_PARTS_UPLOAD * DEFAULT_MAX_NUM_THREADS);
        mainThreadExecutor = new MainThreadExecutor();

    }

    @Override
    public ThreadPoolExecutor forUploadTasks() {
        return networkExecutor;
    }

    @Override
    public ThreadPoolExecutor forBackgroundTasks() {
        return backgroundExecutor;
    }

    @Override
    public Executor forMainThreadTasks() {
        return mainThreadExecutor;
    }

    @Override
    public ThreadPoolExecutor forUploadParts() {
        return partUploadExecutor;
    }
}
