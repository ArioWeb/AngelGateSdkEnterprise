

package com.angelsgate.sdk.AngelsGateUpload.Uploader.Listners;


import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;

public interface OnUploadFailedListener {

    void onUploadFailed(UploadException e);

}
