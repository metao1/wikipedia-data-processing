package org.example.main.connectivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StreamConnectionService implements ConnectionService {

    public InputStream get(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setReadTimeout(10000000);
        return con.getInputStream();
    }
}
