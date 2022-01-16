package com.top.wiki;

import com.top.wiki.connectivity.MockedBasicInputService;
import com.top.wiki.util.GzipStreamFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNull;

public class GzipStreamFactoryTest {

    @Test
    public void givenUrl_whenConvertingToString_thenIsSuccess() {
        var connectionService = new MockedBasicInputService();
        var path = Paths.get("src", "test", "resources", "test.gz");
        try (InputStream inputStream = connectionService.get(path.toFile().getAbsolutePath())) {
            var stringStream = GzipStreamFactory.convertGzipStreamToString(inputStream);
            System.out.println("stringStream = " + stringStream);
        } catch (IOException ex) {
            assertNull(ex.getMessage());
        }
    }
}
