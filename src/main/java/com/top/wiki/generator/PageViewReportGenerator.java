package com.top.wiki.generator;

import com.top.wiki.connectivity.PageViewConnector;
import com.top.wiki.filter.FilterService;
import com.top.wiki.model.LogEntry;
import com.top.wiki.model.PageViewItem;
import com.top.wiki.util.Constants;
import com.top.wiki.util.FileUtils;
import com.top.wiki.util.LogEntryUtils;
import com.top.wiki.util.WikiStringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple2;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * An abstract class that defines the reporting procedure for Top Page View visitors of Wikipedia
 */
@Slf4j
@RequiredArgsConstructor
public abstract class PageViewReportGenerator {
    protected final PageViewConnector wikiConnector;
    protected final FilterService<LogEntry> filterService;
    protected final int threshold;

    public List<Path> execute(List<LocalDateTime> timeList) {
        checkIfWikiPageViewDirExist();
        return timeList.stream()
                .map(LogEntryUtils::urlToPathTuple)
                .map(this::getSortedEntries)
                .peek(this::saveToDevice)
                .filter(Objects::nonNull)
                .map(Tuple2::getT2)
                .toList();
    }

    protected abstract void saveToDevice(Tuple2<Set<LogEntry>, Path> entries);

    protected abstract void exit() throws InterruptedException;

    private void checkIfWikiPageViewDirExist() {
        FileUtils.checkDirectory(Path.of(Constants.WIKIPEDIA_PAGE_VIEW_DIR, ":"));
    }

    protected abstract Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple);

    public List<LogEntry> fetchWikiPageViews(final String url) {
        try (PageViewConnector s = wikiConnector.fetchLogEntries(url)) {
            return s.map(LogEntry::mapToLogEntry);
        } catch (Exception e) {
            throw new RuntimeException("could not convert raw entry into LogEntry, " + e.getMessage());
        }
    }

    public Set<LogEntry> calculateSortedSetLogEntries(List<LogEntry> logEntries) {
        var pageViewReportCalculator = new PageViewReportCalculator();
        pageViewReportCalculator.calculate(logEntries);
        var map = pageViewReportCalculator.getMap();
        log.info("Processed {} entries in total.", map.size());
        // while adding to sorted set it will be sorted. look at compare() function in LogEntry to see the logic
        Set<LogEntry> sortedSet = new TreeSet<>();
        for (PageViewItem pvi : map.values()) {
            sortedSet.addAll(pvi.getLogEntries());
        }
        return sortedSet;
    }

    public void filterBlackList(List<LogEntry> logEntries, String url) {
        var id = WikiStringUtils.extractIdentification(url);
        log.info("Thread {} started filtering {}", Thread.currentThread().getName(), id);
        filterService.applyFilteredEntries(Constants.BLACKLIST_URL, logEntries);
    }

}
