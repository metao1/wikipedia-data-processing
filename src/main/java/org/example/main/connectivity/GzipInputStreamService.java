package org.example.main.connectivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Defines Gzip Input streaming implementation.
 */
public class GzipInputStreamService implements InputStreamConnectionService {

    @Override
    public InputStream get(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setReadTimeout(10000000);
        return con.getInputStream();
    }
}
