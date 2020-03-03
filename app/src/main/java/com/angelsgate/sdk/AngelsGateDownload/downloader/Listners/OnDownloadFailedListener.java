
package com.angelsgate.sdk.AngelsGateDownload.downloader.Listners;

import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;



public interface OnDownloadFailedListener {

    void onDownloadFailed(DownloadException e);

}
