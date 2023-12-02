package com.top.wiki.connectivity;

import com.top.wiki.util.Constants;
import com.top.wiki.util.GzipStreamUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Wikipedia connection to PageViewConnect remote servers
 */
@Slf4j
@RequiredArgsConstructor
public class PageViewConnector implements AutoCloseable {

    // an interface to which the wikipedia page view files is located
    private Stream<String> pageViewStream;
    private final HttpConnection connectionService;

    /**
     * Fetches logentries from remote url address and includes several operation.
     * First it connects to remote server and reads the file content as Gzip input format
     * Then it uncompress the file, and convert it to String format.
     * Then it convert them to model of List of logentries
     *
     * @param wikiUrl as remote url address of wikipedia page view
     * @return list of unsorted parsed log entries
     */
    public PageViewConnector fetchLogEntries(String wikiUrl) throws MalformedURLException {
        if (!wikiUrl.matches(Constants.URL_REGEX)) {
            throw new MalformedURLException(String.format("The url %s is not accepted.", wikiUrl));
        }
        try (InputStream inputStream = connectionService.read(wikiUrl)) {
            pageViewStream = GzipStreamUtil.convertGzipStreamToString(inputStream);
            if (pageViewStream == null) {
                throw new RuntimeException(String.format("The stream for %s was null.", wikiUrl));
            }
        } catch (IOException e) {
            log.info("fetching wiki page: {}.", e.getMessage());
        }
        return this;
    }

    public <T> T map(Function<Stream<String>, T> function) {
        return function.apply(pageViewStream);
    }

    @Override
    public void close() {
        if (pageViewStream != null) {
            pageViewStream.close();
        }
    }
}
