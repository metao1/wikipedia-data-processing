package com.top.wiki.connectivity;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

/**
 * Defines Gzip Input streaming implementation using OkHttp library that supports gzip
 * out of the box. It also supports http/2 protocol for compression
 */
@Slf4j
public class GzipHttp extends HttpConnection {

    @Override
    public Request createRequest(String url, String encoding) {
        return new Request.Builder()
                .header("Content-Encoding", encoding)
                .header("Accept-Encoding", encoding)
                .get()
                .url(url)
                .build();
    }
}
