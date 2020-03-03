package com.angelsgate.sdk.AngelsGateDownload.downloader.core;

public class DownloadCore {


    private static DownloadCore instance = null;
    private final ExecutorSupplier executorSupplier;

    private DownloadCore() {
        this.executorSupplier = new DefaultExecutorSupplier();
    }

    public static DownloadCore getInstance() {
        if (instance == null) {
            synchronized (DownloadCore.class) {
                if (instance == null) {
                    instance = new DownloadCore();
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
