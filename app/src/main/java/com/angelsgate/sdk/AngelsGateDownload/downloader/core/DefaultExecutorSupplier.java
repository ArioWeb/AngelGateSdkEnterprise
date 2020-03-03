

package com.angelsgate.sdk.AngelsGateDownload.downloader.core;

import android.os.Process;

import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.ComponentHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultExecutorSupplier implements ExecutorSupplier {

    private static final int DEFAULT_MAX_NUM_THREADS = ComponentHolder.getInstance().getDownloadTasks();
    private static final int DEFAULT_MAX_NUM_THREADS_FOR_PARTS_DOWNLOAD = ComponentHolder.getInstance().getDownloadThread();

    private final ThreadPoolExecutor networkExecutor;
    private final ThreadPoolExecutor backgroundExecutor;
    private final Executor mainThreadExecutor;
    private final ThreadPoolExecutor partDownloadExecutor;

    DefaultExecutorSupplier() {
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        networkExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS);
        partDownloadExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS_FOR_PARTS_DOWNLOAD);
        backgroundExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS_FOR_PARTS_DOWNLOAD * DEFAULT_MAX_NUM_THREADS);
        mainThreadExecutor = new MainThreadExecutor();

    }

    @Override
    public ThreadPoolExecutor forDownloadTasks() {
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
    public ThreadPoolExecutor forDownloadParts() {
        return partDownloadExecutor;
    }
}
