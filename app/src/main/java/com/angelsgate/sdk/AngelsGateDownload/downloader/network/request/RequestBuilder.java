

package com.angelsgate.sdk.AngelsGateDownload.downloader.network.request;


import com.angelsgate.sdk.AngelsGateDownload.downloader.Priority;



public interface RequestBuilder {


    RequestBuilder setPriority(Priority priority);


    RequestBuilder setReadTimeout(int readTimeout);

    RequestBuilder setConnectTimeout(int connectTimeout);

    RequestBuilder setUserAgent(String userAgent);

}
