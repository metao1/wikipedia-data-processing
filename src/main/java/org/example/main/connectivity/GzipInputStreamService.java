package org.example.main.connectivity;

import okhttp3.*;
import org.example.main.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Defines Gzip Input streaming implementation using OkHttp library that supports gzip
 * out of the box. It also supports http/2 protocol for compression
 */
public class GzipInputStreamService implements InputStreamConnectionService {
    private final OkHttpClient client;

    public GzipInputStreamService() {
        client = new OkHttpClient.Builder().readTimeout(Constants.TIMEOUT)
                .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();
    }

    @Override
    public InputStream get(String strUrl) throws IOException {
        Request request = new Request.Builder()
                .header("Content-Encoding", "gzip")
                .header("Accept-Encoding", "gzip")
                .get()
                .url(strUrl)
                .build();
        Response execute = client.newCall(request).execute();
        ResponseBody body = execute.body();
        Objects.requireNonNull(body, "returned body for request was null or empty");
        return body.byteStream();
    }
}
