package org.example.main.connectivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GzipConnectionService implements ConnectionService {

    @Override
    public InputStream get(String strUrl) throws IOException {
        //using async client
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setReadTimeout(10000000);
        return con.getInputStream();
    }
}
