

package com.angelsgate.sdk.AngelsGateUpload.Uploader.core;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public interface ExecutorSupplier {

    ThreadPoolExecutor forUploadTasks();

    ThreadPoolExecutor forBackgroundTasks();

    Executor forMainThreadTasks();

    ThreadPoolExecutor forUploadParts();

}
