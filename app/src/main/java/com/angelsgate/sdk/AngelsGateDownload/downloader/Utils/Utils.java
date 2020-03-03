/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.angelsgate.sdk.AngelsGateDownload.downloader.Utils;


import com.angelsgate.sdk.AngelsGateDownload.downloader.core.DownloadCore;
import com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException;
import com.angelsgate.sdk.AngelsGateDownload.downloader.internal.ComponentHolder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.angelsgate.sdk.AngelsGateDownload.downloader.exception.DownloadException.EXCEPTION_IO_EXCEPTION;


public final class Utils {


    private Utils() {
        // no instance
    }

    public static String getPath(String dirPath, String fileName,String handler,String fileExtention) {
        return dirPath + File.separator + fileName+ handler +"."+fileExtention;
    }

    public static String getTempPath(String dirPath, String fileName,String handler, String fileExtention) {
        return getPath(dirPath, fileName,handler,fileExtention) + ".temp";
    }


    public static void renameFileName(String oldPath, String newPath) throws IOException {
        final File oldFile = new File(oldPath);
        try {
            final File newFile = new File(newPath);
            if (newFile.exists()) {
                if (!newFile.delete()) {
                    throw new DownloadException(EXCEPTION_IO_EXCEPTION,"File Delete Failed");
                }
            }
            if (!oldFile.renameTo(newFile)) {
                throw new DownloadException(EXCEPTION_IO_EXCEPTION,"Rename Failed");

            }
        } finally {
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
    }


    public static void deleteFile(String Path) throws IOException {
        final File File = new File(Path);

        if (File.exists()) {
            if (!File.delete()) {
                throw new DownloadException(EXCEPTION_IO_EXCEPTION,"File Delete Failed");
            }
        }

    }


    public static void deleteTempFileAndDatabaseEntryInBackground(final String temppath,final String downloadId) {

        DownloadCore.getInstance().getExecutorSupplier().forBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        ComponentHolder.getInstance().getDbHelper().removeDownloadRequest(downloadId);
                        File file = new File(temppath);
                        if (file.exists()) {
                            file.delete();
                        }


                    }
                });

    }


    public static String getUniqueId(String handler, String dirPath, String fileName) {

        String string = handler + File.separator + dirPath + File.separator + fileName;

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
