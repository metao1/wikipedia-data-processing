package com.top.wiki.connectivity;

import com.top.wiki.model.LogEntry;
import com.top.wiki.util.Constants;
import com.top.wiki.util.GzipStreamFactory;
import com.top.wiki.util.LogEntryUtils;
import com.top.wiki.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Wikipedia connection to PageViewConnect remote servers
 */
public class WikipediaPageViewConnectService {

    // an interface to which the wikipedia page view files is located
    private final InputStreamConnectionService connectionService;

    public WikipediaPageViewConnectService(InputStreamConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Fetches logentries from remote url address and includes several operation.
     * First it connects to remote server and reads the file content as Gzip input format
     * Then it uncompress the file, and convert it to String format.
     * Then it convert them to model of List of logentries
     *
     * @param wikiPageViewStringUrl as remote url address of wikipedia page view
     * @return list of unsorted parsed log entries
     */
    public List<LogEntry> fetchLogEntries(String wikiPageViewStringUrl) {
        List<LogEntry> mapEntries = null;
        try {
            boolean notUrlMatch = !wikiPageViewStringUrl.matches(Constants.URL_REGEX);
            if (notUrlMatch) {
                throw new MalformedURLException(String.format("The url %s is not accepted.", wikiPageViewStringUrl));
            }
            var id = StringUtils.extractIdentification(wikiPageViewStringUrl);
            System.out.printf("Thread %s start downloading from %s%n", Thread.currentThread().getName(), wikiPageViewStringUrl);
            try (InputStream inputStream = connectionService.get(wikiPageViewStringUrl)) {
                System.out.printf("Thread %s finished downloading %s%n", Thread.currentThread().getName(), wikiPageViewStringUrl);
                System.out.printf("Thread %s started processing %s%n", Thread.currentThread().getName(), id);
                Stream<String> strWikiViewPageStream = GzipStreamFactory.convertGzipStreamToString(inputStream);
                if (strWikiViewPageStream == null) {
                    throw new RuntimeException(String.format("The stream for %s was null.", wikiPageViewStringUrl));
                }
                System.out.printf("Thread %s finished downloading.%s%n", Thread.currentThread().getName(), wikiPageViewStringUrl);
                System.out.printf("Thread %s started parsing %s inputs.%n", Thread.currentThread().getName(), id);
                mapEntries = LogEntryUtils.mapToLogEntry(strWikiViewPageStream);
                System.out.printf("Thread %s finished %s processing.%n", Thread.currentThread().getName(), id);
            } catch (IOException e) {
                System.err.printf("error while fetching wikepage: message: %s.%n", e.getMessage());
            }
        } catch (IOException ex) {
            System.err.println("error while connecting to wikipedia services:" + ex.getMessage());
            return null;
        }
        return mapEntries;
    }

}
