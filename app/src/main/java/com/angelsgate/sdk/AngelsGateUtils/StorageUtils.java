package com.angelsgate.sdk.AngelsGateUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StorageUtils {


    public static String getInternalFilesDirectory(Context mContext) {
        return mContext.getFilesDir().getAbsolutePath();

    }


    public static String getInternalRootDirectory() {
        return Environment.getRootDirectory().getAbsolutePath();
    }


    public static void WriteByteFile(byte[] bytes, String ScreenDirectory, String childPhone) throws IOException {
        FileOutputStream fos = null;

        File pictureFile = getOutputMediaFile(ScreenDirectory, childPhone);
        if (pictureFile == null) {
            return;
        }

        if (bytes == null || !(bytes.length > 0)) {
            return;
        }

        try {

            fos = new FileOutputStream(pictureFile);
            fos.write(bytes);


        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
        }
    }


    public static File getOutputMediaFile(String ScreenDirectory, String childPhone) {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(ScreenDirectory, childPhone + "_IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }


    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path, "profile.jpg");

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public static Bitmap loadBitmapFromFile(File f) {



        try {

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void getDirectoryFilesImpl(File directory, List<File> out) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            } else {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        getDirectoryFilesImpl(files[i], out);
                    } else {
                        out.add(files[i]);
                    }
                }
            }
        }
    }


    public boolean createDirectory(String path, boolean override, String childphone) {

        // Check if directory exists. If yes, then delete all directory
        if (override && isDirectoryExists(path)) {
            deleteDirectory(path, childphone);
        }

        // Create new directory
        return createDirectory(path);
    }

    public static boolean createDirectory(String path) {
        File directory = new File(path);
        if (directory.exists()) {

            return false;
        }
        return directory.mkdirs();
    }

    public static boolean createDirectory(File directory) {
        if (directory.exists()) {
            return false;
        }
        return directory.mkdirs();
    }


    public boolean deleteDirectory(String path, String childphone) {
        return deleteDirectoryImpl(path, childphone);
    }

    public boolean isDirectoryExists(String path) {
        return new File(path).exists();
    }


    public static boolean deleteDirectoryImpl(String path, String childphone) {
        File directory = new File(path);

        // If the directory exists then delete
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }
            // Run on all sub files and folders and delete them
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectoryImpl(files[i].getAbsolutePath(), childphone);
                } else {
                    if (files[i].getName().startsWith(childphone))
                        files[i].delete();
                }
            }
        }
        return directory.delete();
    }


    public File getFile(String path) {
        return new File(path);
    }

    public List<File> getFiles(String dir) {
        return getFiles(dir, null);
    }

    public List<File> getFiles(String dir, final String matchRegex) {
        File file = new File(dir);
        File[] files = null;
        if (matchRegex != null) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String fileName) {
                    return fileName.matches(matchRegex);
                }
            };
            files = file.listFiles(filter);
        } else {
            files = file.listFiles();
        }
        return files != null ? Arrays.asList(files) : null;
    }


    public Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
