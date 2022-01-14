package org.example.main;

import org.apache.commons.io.IOUtils;
import org.example.main.connectivity.GzipConnectionService;
import org.example.main.connectivity.StreamConnectionService;
import org.example.main.model.LogEntry;
import org.example.main.util.FileUtils;
import org.example.main.util.LogEntryUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpConnectionTest {

    @Test
    void givenUrl_getSimpleFile_isSuccess() throws IOException {
        var httpConnection = new StreamConnectionService();
        InputStream inputStream = httpConnection.get("https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages");
        assertNotNull(inputStream);
        FileUtils.copyInputStreamToFile(inputStream, Path.of("blacklists", "blacklist.txt"));
    }

    @Test
    public void givenUrl_checkGzipConnectionService_thenSuccessful() throws IOException {
        var connectionService = new GzipConnectionService();
        InputStream inputStream = connectionService.get("https://dumps.wikimedia.org/other/pageviews/2019/2019-02/pageviews-20190201-000000.gz");
        try (GZIPInputStream gzipStream = new GZIPInputStream(inputStream)) {
            String s = IOUtils.toString(gzipStream, StandardCharsets.UTF_8);
            List<LogEntry> logEntryList = LogEntryUtils.mapToLogEntry(s);
            System.out.println(logEntryList.size());
        } catch (Exception ignore) {

        }
    }

    @Test
    public void givenUrl_whenGzipInputStream_thenSuccess() throws IOException {
        URL url = new URL("https://dumps.wikimedia.org/other/pageviews/2019/2019-02/pageviews-20190201-000000.gz");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setReadTimeout(10000000);
        String s;
        try (InputStream inputStream = con.getInputStream();
             GZIPInputStream gzipStream = new GZIPInputStream(inputStream)) {
            s = IOUtils.toString(gzipStream, StandardCharsets.UTF_8);
        }
        List<LogEntry> logEntryList = LogEntryUtils.mapToLogEntry(s);
        System.out.println(logEntryList.size());
    }

}
