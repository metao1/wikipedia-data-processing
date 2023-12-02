package com.top.wiki;

import com.top.wiki.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpConnectionTest {

    @Test
    void givenUrl_getSimpleFile_isSuccess() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("blacklists.txt");
        assertNotNull(inputStream);
        FileUtils.copyInputStreamToFile(inputStream, Path.of("blacklists", "blacklists.txt"));
    }

    @Test
    public void givenUrl_whenGzipInputStream_thenSuccess() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.gz");
        assertNotNull(inputStream);
    }

}
