package org.example.main.connectivity;

import org.example.main.model.LogEntry;
import org.example.main.util.Constants;
import org.example.main.util.DateTimeUtil;
import org.example.main.util.LogEntryUtils;
import org.example.main.util.StringUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.main.util.StreamUtils.convertToString;


public class WikipediaPageViewConnectService {

    private final ConnectionService connectionService;

    public WikipediaPageViewConnectService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public Tuple2<Path, String> buildPageViewUriEntry(LocalDateTime time, String outputStrPath) {
        var hour = DateTimeUtil.pad(time.getHour());
        var year = DateTimeUtil.pad(time.getYear());
        var month = DateTimeUtil.pad(time.getMonthValue());
        var day = DateTimeUtil.pad(time.getDayOfMonth());
        var url = StringUtils.buildStringWikiPageViewUrl(year, month, day, hour);
        var fileName = StringUtils.buildStringWikiPageViewFilename(year, month, day, hour);
        final Path path;
        if (StringUtils.notBlank(outputStrPath)) {
            path = Path.of(outputStrPath, fileName);
        } else {
            path = Path.of(fileName);
        }
        return Tuples.of(path, url);
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
            System.out.printf("Thread %s started processing %s.%n", Thread.currentThread().getName(), id);
            String strWikiViewPage = convertToString(inputStream);
            mapEntries = LogEntryUtils.mapToLogEntry(strWikiViewPage);
            System.out.printf("Thread %s finished processing %s%n", Thread.currentThread().getName(), id);;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
        return mapEntries;
    }

}
