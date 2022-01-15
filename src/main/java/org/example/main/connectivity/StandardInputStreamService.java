package org.example.main.connectivity;

import org.example.main.util.Constants;

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

    public StandardInputStreamService() {
        client = HttpClient.newBuilder()
                .connectTimeout(Constants.TIMEOUT)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

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
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
