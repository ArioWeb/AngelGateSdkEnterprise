
package com.angelsgate.sdk.AngelsGateDownload.downloader.core;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;



public interface ExecutorSupplier {

    ThreadPoolExecutor forDownloadTasks();

    ThreadPoolExecutor forBackgroundTasks();

    Executor forMainThreadTasks();

    ThreadPoolExecutor forDownloadParts();

}
