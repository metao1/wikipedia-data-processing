package com.top.wiki;

import com.top.wiki.util.GzipStreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
class GzipStreamFactoryTest {

    @Test
    void givenUrl_whenConvertingToString_thenIsSuccess() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.gz")) {
            var stringStream = GzipStreamUtil.convertGzipStreamToString(inputStream);
            log.info("stringStream = " + stringStream.toList());
        } catch (IOException ex) {
            assertNull(ex.getMessage());
        }
    }
}
