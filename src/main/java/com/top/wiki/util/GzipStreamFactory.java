package com.top.wiki.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Factory class of conversions the GzipInputStream
 */
public class GzipStreamFactory {

    /**
     * Converts the input as GzipInputStream to Stream of Strings
     * This first deflate the InputStream to String in memory and then uses
     * Apache IOUtils library to read from the input stream in UTF-8 format
     * @param in input stream to read from
     * @return the stream of strings from the source
     * @throws IOException if can't read from input stream
     */
    public static Stream<String> convertGzipStreamToString(InputStream in) throws IOException {
        try (GZIPInputStream is = new GZIPInputStream(in)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8).lines();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
