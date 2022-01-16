package com.top.wiki;

import com.top.wiki.connectivity.MockedBasicInputService;
import com.top.wiki.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpConnectionTest {

    @Test
    void givenUrl_getSimpleFile_isSuccess() throws IOException {
        var httpConnection = new MockedBasicInputService();
        var path = Paths.get("src", "test", "resources", "blacklists.txt");
        InputStream inputStream = httpConnection.get(path.toFile().getAbsolutePath());
        assertNotNull(inputStream);
        FileUtils.copyInputStreamToFile(inputStream, Path.of("blacklists", "blacklists.txt"));
    }

    @Test
    public void givenUrl_whenGzipInputStream_thenSuccess() throws IOException {
        var connectionService = new MockedBasicInputService();
        var path = Paths.get("src", "test", "resources", "test.gz");
        InputStream inputStream = connectionService.get(path.toFile().getAbsolutePath());
        assertNotNull(inputStream);
    }

}
