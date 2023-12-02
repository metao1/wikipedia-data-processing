package com.top.wiki.connectivity;

import com.top.wiki.model.RemoteFileDoesNotExits;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * An interface that turns any url to InputStream
 * for defining connection methods to deal with Network connection.
 */
public abstract class HttpConnection implements RequestMapper {

    protected final OkHttpClient client;

    /**
     * Gets an InputStream from url of remote host in Network.
     *
     * @param url Address of the remote host
     * @return InputStream after establish connection to remote host.
     * @throws IOException if any connection problem occurs
     */
    public InputStream read(String url) throws IOException {
        Response execute = client.newCall(createRequest(url, "gzip")).execute();
        ResponseBody body = execute.body();
        if (execute.code() != 200) {
            throw new RemoteFileDoesNotExits(String.format("%s, does not exists on server", url));
        }
        Objects.requireNonNull(body, "returned body for request was null or empty");
        return body.byteStream();
    }

    HttpConnection() {
        client = new OkHttpClient.Builder().build();
    }
}
