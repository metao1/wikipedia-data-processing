package org.example.main.connectivity;

import org.example.main.model.LogEntry;
import org.example.main.util.*;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.example.main.util.Constants.WIKIPEDIA_PAGE_VIEW_DIR;

public class WikipediaPageViewConnectService {

    private final ConnectionService connectionService;

    public WikipediaPageViewConnectService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public Tuple2<String, Path> urlToPathTuple(LocalDateTime time) {
        var hour = DateTimeUtil.pad(time.getHour());
        var year = DateTimeUtil.pad(time.getYear());
        var month = DateTimeUtil.pad(time.getMonthValue());
        var day = DateTimeUtil.pad(time.getDayOfMonth());
        var fileName = StringUtils.buildStringWikiPageViewFilename(year, month, day, hour);
        Path outputPath = Path.of(WIKIPEDIA_PAGE_VIEW_DIR, fileName);
        String url = StringUtils.buildStringWikiPageViewUrl(year, month, day, hour);
        return Tuples.of(url, outputPath);
    }

    public List<LogEntry> fetchLogEntries(String wikiPageViewStringUrl) {
        final List<LogEntry> mapEntries;
        try {
            boolean notUrlMatch = !wikiPageViewStringUrl.matches(Constants.URL_REGEX);
            if (notUrlMatch) {
                throw new MalformedURLException(String.format("The url %s is not accepted.", wikiPageViewStringUrl));
            }
            var id = StringUtils.extractIdentification(wikiPageViewStringUrl);
            System.out.printf("Thread %s start downloading from %s%n", Thread.currentThread().getName(), wikiPageViewStringUrl);
            InputStream inputStream = connectionService.get(wikiPageViewStringUrl);
            System.out.printf("Thread %s finished downloading %s%n", Thread.currentThread().getName(), wikiPageViewStringUrl);
            System.out.printf("Thread %s started processing %s%n", Thread.currentThread().getName(), id);
            Stream<String> strWikiViewPageStream = GzipStreamFactory.convertGzipStreamToString(inputStream);
            if (strWikiViewPageStream == null) {
                throw new RuntimeException(String.format("The stream for %s was null.", wikiPageViewStringUrl));
            }
            mapEntries = LogEntryUtils.mapToLogEntry(strWikiViewPageStream);
            System.out.printf("Thread %s finished processing %s%n", Thread.currentThread().getName(), id);

        } catch (IOException ex) {
            System.err.println("error while connecting to wikipedia services:" + ex.getMessage());
            return null;
        }
        return mapEntries;
    }

}
