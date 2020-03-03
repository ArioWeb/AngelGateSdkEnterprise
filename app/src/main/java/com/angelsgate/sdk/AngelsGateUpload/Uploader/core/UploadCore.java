package com.angelsgate.sdk.AngelsGateUpload.Uploader.core;

public class UploadCore {


    private static UploadCore instance = null;
    private final ExecutorSupplier executorSupplier;

    private UploadCore() {
        this.executorSupplier = new DefaultExecutorSupplier();
    }

    public static UploadCore getInstance() {
        if (instance == null) {
            synchronized (UploadCore.class) {
                if (instance == null) {
                    instance = new UploadCore();
                }
            }
        }
        return instance;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return executorSupplier;
    }

    public static void shutDown() {
        if (instance != null) {
            instance = null;
        }
    }
}
