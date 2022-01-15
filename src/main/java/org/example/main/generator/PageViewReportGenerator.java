package org.example.main.generator;

import org.example.main.connectivity.WikipediaPageViewConnectService;
import org.example.main.filter.FilterService;
import org.example.main.model.LogEntry;
import org.example.main.model.PageViewItem;
import org.example.main.util.CollectionUtils;
import org.example.main.util.Constants;
import org.example.main.util.FileUtils;
import org.example.main.util.StringUtils;
import reactor.util.function.Tuple2;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public abstract class PageViewReportGenerator {
    protected final WikipediaPageViewConnectService wikiConnector;
    protected final FilterService<LogEntry> filterService;
    protected final int threshold;

    public PageViewReportGenerator(WikipediaPageViewConnectService connectService, FilterService<LogEntry> filterService, int threshold) {
        this.filterService = filterService;
        this.wikiConnector = connectService;
        this.threshold = threshold;
    }

    public List<Path> execute(List<LocalDateTime> timeList) throws InterruptedException {
        checkIfWikiPageViewDirExist();
        List<Path> collect = timeList.parallelStream()
                .map(this::getUrlPathTuple)
                .map(this::getSortedEntries)
                .peek(this::saveToDevice)
                .filter(Objects::nonNull)
                .map(Tuple2::getT2)
                .collect(Collectors.toList());
        exit();
        return collect;
    }

    protected abstract void saveToDevice(Tuple2<Set<LogEntry>, Path> entries);

    protected abstract void exit() throws InterruptedException;


    private void checkIfWikiPageViewDirExist() {
        FileUtils.checkDirectory(Path.of(Constants.WIKIPEDIA_PAGE_VIEW_DIR, ":"));
    }

    protected abstract Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple);

    public Set<LogEntry> fetchWikiPageViews(final String url) {
        var id = StringUtils.extractIdentification(url);
        List<LogEntry> logEntries = wikiConnector.fetchLogEntries(url);
        List<LogEntry> nonEmpty = CollectionUtils.nonEmptyListConvertor(logEntries);
        List<LogEntry> filteredLogEntries = filterBlackList(nonEmpty, id);
        return mapSortingLogEntries(filteredLogEntries);
    }

    public Set<LogEntry> mapSortingLogEntries(List<LogEntry> logEntries) {
        ConcurrentMap<String, PageViewItem> sortedSetMap = new ConcurrentHashMap<>();
        int processedEntriesNum = calcUpdateTopPageViewsMap(logEntries, sortedSetMap);
        System.out.printf("Processed %d entries in total.%n", processedEntriesNum);
        Set<LogEntry> sortedSet = new TreeSet<>();
        for (PageViewItem pvi : sortedSetMap.values()) {
            sortedSet.addAll(pvi.getLogEntries());
        }
        return sortedSet;
    }

    private int calcUpdateTopPageViewsMap(List<LogEntry> logEntries, ConcurrentMap<String, PageViewItem> map) {
        var i = 0;
        for (LogEntry le : logEntries) {
            var dc = le.getDomainCode();
            if (map.containsKey(dc)) {
                var pageViewItem = map.get(dc);
                var minPageViewLe = pageViewItem.minLogEntryPageView();
                if (minPageViewLe == null) {
                    return 0;
                }
                if (le.getCountViews() >= minPageViewLe.getCountViews()) {
                    pageViewItem.addNewItem(le);
                    var n = pageViewItem.getMaxEntryCount();
                    if (threshold <= n) {  // Remove the minimum entry when is over k items
                        pageViewItem.removeLogEntry(minPageViewLe);
                    }
                    pageViewItem.updateMaxEntryCount();
                    map.put(dc, pageViewItem);
                }
            } else {
                var set = new TreeSet<LogEntry>();
                set.add(le);
                var nle = new PageViewItem(1, set);
                map.put(dc, nle);
            }
            if (i++ % 1000000 == 0) System.out.printf("Processed %d entries%n", i);
        }
        return i;
    }

    private List<LogEntry> filterBlackList(List<LogEntry> logEntries, String id) {
        System.out.printf("Thread %s started filtering %s%n", Thread.currentThread().getName(), id);
        return filterService.applyAndGetFilteredEntries(Constants.BLACKLIST_URL, logEntries);
    }

    private Tuple2<String, Path> getUrlPathTuple(LocalDateTime timeList) {
        return wikiConnector.urlToPathTuple(timeList);
    }

}
