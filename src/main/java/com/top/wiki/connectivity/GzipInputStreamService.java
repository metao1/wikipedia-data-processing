package com.top.wiki.connectivity;

import com.top.wiki.model.RemoteFileDoesNotExits;
import com.top.wiki.util.Constants;
import okhttp3.*;

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

    /**
     * Gets inputstream of url
     *
     * @param strUrl the source url of remote server
     * @return input stream of source
     * @throws IOException thrown if can't read from source
     */
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
        if (execute.code() != 200) {
            throw new RemoteFileDoesNotExits(String.format("file %s, does not exists on server", strUrl));
        }
        Objects.requireNonNull(body, "returned body for request was null or empty");
        return body.byteStream();
    }
}
