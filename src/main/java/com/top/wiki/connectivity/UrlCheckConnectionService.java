package com.top.wiki.connectivity;

import com.top.wiki.util.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;

public class UrlCheckConnectionService implements CheckConnectionService {
    private final HttpClient client;

    public UrlCheckConnectionService() {
        client = HttpClient.newBuilder()
                .connectTimeout(Constants.TIMEOUT)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Override
    public boolean exists(String strUrl) throws IOException {
        final URL url = new URL(strUrl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("HEAD");
        int responseCode = huc.getResponseCode();
        return responseCode == 200;
    }
}
