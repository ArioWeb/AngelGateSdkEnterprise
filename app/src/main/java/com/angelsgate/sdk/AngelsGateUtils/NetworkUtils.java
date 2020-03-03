package com.angelsgate.sdk.AngelsGateUtils;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public class NetworkUtils {

    // create RequestBody instance from string
    public static RequestBody stringToRequestBody(String data) {
        return RequestBody.create(MediaType.parse("text/plain"), data);
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


    public static Request CreateRequestForSocket(String requestBody) {
        //build the request
        return new Request.Builder().url("").post(NetworkUtils.stringToRequestBody(requestBody)).build();
    }


    public static String ConvertObjectToString(Object data) {

        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
