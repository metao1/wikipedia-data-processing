package com.top.wiki.connectivity;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

/**
 * Standard Simple InputStream service that only implements
 * connections for acquiring the remote input stream
 */
@Slf4j
public class StandardHttp extends HttpConnection {

    @Override
    public Request createRequest(String url, String encoding) {
        return new Request.Builder()
                .get()
                .url(url)
                .build();
    }
}
