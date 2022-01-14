package org.example.main.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class StreamUtils {

    public static String convertToString(InputStream is) throws IOException {
        try (GZIPInputStream gzipStream = new GZIPInputStream(is)) {
            return IOUtils.toString(gzipStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (e instanceof ZipException) {
                return IOUtils.toString(is, StandardCharsets.UTF_8);
            }
            System.err.println(e.getMessage());
            return "";
        }
    }
    public static void saveInputStreamToFile(InputStream is, Path blacklistPath) {
        FileUtils.copyInputStreamToFile(is, blacklistPath);
    }
}
