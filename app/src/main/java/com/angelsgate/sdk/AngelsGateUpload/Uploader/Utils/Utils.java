

package com.angelsgate.sdk.AngelsGateUpload.Uploader.Utils;


import com.angelsgate.sdk.AngelsGateUpload.Uploader.core.UploadCore;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException;
import com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.ComponentHolder;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.angelsgate.sdk.AngelsGateUpload.Uploader.exception.UploadException.EXCEPTION_IO_EXCEPTION;




public final class Utils {


    private Utils() {
        // no instance
    }




    public static String getUniqueId( String dirPath, String fileName) {

        String string =   dirPath + File.separator + fileName;

        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return String.valueOf(hex.toString().hashCode());
    }



}
