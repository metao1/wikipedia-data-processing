package com.top.wiki.connectivity;

import com.top.wiki.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Standard Simple InputStream service that only implements
 * connections for acquiring the remote input stream
 */
public class StandardInputStreamService implements InputStreamConnectionService {
    private final HttpClient client;

    /**
     * Standard Input Stream Service uses Http/2 protocol and java 11 HttpClient
     */
    public StandardInputStreamService() {
        client = HttpClient.newBuilder()
                .connectTimeout(Constants.TIMEOUT)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    /**
     * Gets inputstream of url
     *
     * @param strUrl page view url
     * @return the inputstream of desired pageview url
     * @throws IOException thrown if can't read from
     */
    public InputStream get(String strUrl) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(strUrl))
                .build();
        final HttpResponse<InputStream> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return response.body();
        } catch (InterruptedException e) {
            System.err.println("error while reading from remote host" + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
