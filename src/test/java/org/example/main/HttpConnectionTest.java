package org.example.main;

import org.example.main.connectivity.GzipInputStreamService;
import org.example.main.connectivity.StandardInputStreamService;
import org.example.main.util.FileUtils;
import org.example.main.util.GzipStreamFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpConnectionTest {

    @Test
    void givenUrl_getSimpleFile_isSuccess() throws IOException {
        var httpConnection = new StandardInputStreamService();
        InputStream inputStream = httpConnection.get("https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages");
        assertNotNull(inputStream);
        FileUtils.copyInputStreamToFile(inputStream, Path.of("blacklists", "blacklist.txt"));
    }

    @Test
    public void givenUrl_whenGzipInputStream_thenSuccess() throws IOException {
        var connectionService = new GzipInputStreamService();
        InputStream inputStream = connectionService.get("https://dumps.wikimedia.org/other/pageviews/2019/2019-02/pageviews-20190201-000000.gz");
        var stringStream = GzipStreamFactory.convertGzipStreamToString(inputStream);
        assertNotNull(stringStream);
    }

}
