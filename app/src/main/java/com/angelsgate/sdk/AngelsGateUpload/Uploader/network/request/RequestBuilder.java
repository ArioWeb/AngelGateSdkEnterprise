

package com.angelsgate.sdk.AngelsGateUpload.Uploader.network.request;


import com.angelsgate.sdk.AngelsGateUpload.Uploader.Priority;



public interface RequestBuilder {


    RequestBuilder setPriority(Priority priority);


    RequestBuilder setReadTimeout(int readTimeout);

    RequestBuilder setConnectTimeout(int connectTimeout);

    RequestBuilder setUserAgent(String userAgent);

}
