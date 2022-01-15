package org.example.main.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class GzipStreamFactory {

    public static Stream<String> convertGzipStreamToString(InputStream in) throws IOException {
        try (GZIPInputStream is = new GZIPInputStream(in)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8).lines();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
