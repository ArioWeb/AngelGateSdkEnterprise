package com.angelsgate.sdk.AngelsGateUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

public class fileUtils {

    /**
     * API for creating thumbnail from Video
     * @param filePath - video file path
     * @param type - size MediaStore.Images.Thumbnails.MINI_KIND or MICRO_KIND
     * @return thumbnail bitmap
     */
    public static Bitmap createThumbnailFromPath(String filePath, int type){
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }



    /**
     * API for creating thumbnail of specified dimensions from an image file
     * @param filePath - source image path
     * @param width - output image width
     * @param height - output image height
     * @return - thumbnail bitmap of specified dimension
     */
    public static Bitmap createThumbnailFromBitmap(String filePath, int width, int height){
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), width, height);
    }




    /**
     * API for creating thumbnail from existing Bitmap
     * @param source - source Bitmap file
     * @param width - output image width
     * @param height - output image height
     * @return - thumbnail bitmap of specified dimension
     */
    public static Bitmap createThumbnailFromBitmap(Bitmap source, int width, int height){
        //OPTIONS_RECYCLE_INPUT- Constant used to indicate we should recycle the input
        return ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }


}
