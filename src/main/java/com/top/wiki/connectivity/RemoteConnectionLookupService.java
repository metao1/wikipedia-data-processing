package com.top.wiki.connectivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteConnectionLookupService implements UrlCheckoutService {

    @Override
    public boolean exists(String strUrl) throws IOException {
        final URL url = new URL(strUrl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("HEAD");
        int responseCode = huc.getResponseCode();
        return responseCode == 200;
    }
}
