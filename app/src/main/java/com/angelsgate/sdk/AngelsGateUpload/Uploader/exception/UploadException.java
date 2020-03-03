package com.angelsgate.sdk.AngelsGateUpload.Uploader.exception;



import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class UploadException extends RuntimeException {

    /**
     *
     */
    public static final int EXCEPTION_URL_NULL = 0;
    /**
     *
     */
    public static final int EXCEPTION_PATH_NULL = 1;
    /**
     *
     */
    public static final int EXCEPTION_URL_ERROR = 2;
    /**
     *
     */
    public static final int EXCEPTION_SERVER_ERROR = 3;
    /**
     *
     */
    public static final int EXCEPTION_PROTOCOL = 4;
    /**
     *
     */
    public static final int EXCEPTION_IO_EXCEPTION = 5;
    /**
     *
     */
    public static final int EXCEPTION_FILE_SIZE_ZERO = 6;
    /**
     *
     */
    public static final int EXCEPTION_PAUSE = 7;
    /**
     *
     */
    public static final int EXCEPTION_SERVER_SUPPORT_CODE = 8;

    /**
     *
     */
    public static final int EXCEPTION_OTHER = 9;

    public static final int EXCEPTION_CONNECTION_ERROR =10;

    public static final int EXCEPTION_THREADS_NULL_ERROR =11;

    public static final int EXCEPTION_SOME_THREADS_ERROR =12;
    public static final int EXCEPTION_BASIC_ERROR =13;


    private int code;

    public UploadException(@ExceptionType int code) {
        this.code = code;
    }

    public UploadException(@ExceptionType int code, String message) {
        super(message);
        this.code = code;
    }

    public UploadException(@ExceptionType int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public UploadException(@ExceptionType int code, Throwable cause) {
        super(cause);
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Upload exception type.
     */
    @IntDef({EXCEPTION_URL_NULL, EXCEPTION_PATH_NULL, EXCEPTION_URL_ERROR, EXCEPTION_SERVER_ERROR,
            EXCEPTION_PROTOCOL, EXCEPTION_IO_EXCEPTION, EXCEPTION_FILE_SIZE_ZERO, EXCEPTION_PAUSE,
            EXCEPTION_SERVER_SUPPORT_CODE, EXCEPTION_OTHER,EXCEPTION_CONNECTION_ERROR,EXCEPTION_THREADS_NULL_ERROR,EXCEPTION_SOME_THREADS_ERROR,EXCEPTION_BASIC_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExceptionType {

    }

}
