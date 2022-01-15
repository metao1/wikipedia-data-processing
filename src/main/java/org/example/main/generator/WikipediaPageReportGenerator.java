package org.example.main.generator;

import org.example.main.connectivity.WikipediaPageViewConnectService;
import org.example.main.filter.FilterService;
import org.example.main.model.LogEntry;
import org.example.main.model.PageViewItem;
import org.example.main.storage.FileStorage;
import org.example.main.util.CollectionUtils;
import org.example.main.util.Constants;
import org.example.main.util.FileUtils;
import org.example.main.util.StringUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WikipediaPageReportGenerator {

    private final ExecutorService executor;
    private final WikipediaPageViewConnectService wikiOperator;
    private final FilterService<LogEntry> filterService;
    private final FileStorage<Set<LogEntry>, Path> fileStorage;
    private final int threshold;

    public WikipediaPageReportGenerator(FilterService<LogEntry> filterService, WikipediaPageViewConnectService wikiOperator, FileStorage<Set<LogEntry>, Path> fileStorage, int threshold) {
        this.executor = Executors.newFixedThreadPool(3);
        this.filterService = filterService;
        this.wikiOperator = wikiOperator;
        this.fileStorage = fileStorage;
        this.threshold = threshold;
    }

    public List<Path> execute(List<LocalDateTime> timeList) throws InterruptedException {
        FileUtils.checkDirectory(Path.of(Constants.WIKIPEDIA_PAGE_VIEW_DIR, ":"));
        List<Path> executedPaths = timeList
                .parallelStream()
                .map(wikiOperator::urlToPathTuple)
                .map(tuple -> CompletableFuture.supplyAsync(() -> {
                    String url = tuple.getT1();
                    Path filePath = tuple.getT2();
                    boolean missedFile = FileUtils.fileNotExists(filePath);
                    if (!missedFile) {
                        System.out.printf("Skipped processing request %s while file exists%n.", filePath);
                        return null;
                    }
                    Set<LogEntry> logEntries = fetchWikiPageViews(url);
                    return Tuples.of(logEntries, tuple.getT2());
                }, executor))
                .map(CompletableFuture::join)
                .peek(result -> CompletableFuture.runAsync(() -> saveToDevicePipeline(result), executor))
                .filter(Objects::nonNull)
                .map(Tuple2::getT2)
                .collect(Collectors.toList());
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        return executedPaths;
    }

    public void saveToDevicePipeline(Tuple2<Set<LogEntry>, Path> logEntriesPathTuple) {
        if (logEntriesPathTuple == null) {
            return;
        }
        var logEntries = logEntriesPathTuple.getT1();
        var filePath = logEntriesPathTuple.getT2();
        System.out.printf("writing %d records in path: %s%n", logEntries.size(), filePath);
        fileStorage.write(logEntries, filePath);
    }

    public Set<LogEntry> fetchWikiPageViews(final String url) {
        var id = StringUtils.extractIdentification(url);
        List<LogEntry> logEntries = wikiOperator.fetchLogEntries(url);
        List<LogEntry> nonEmpty = CollectionUtils.nonEmptyListConvertor(logEntries);
        List<LogEntry> filteredLogEntries = filterBlackList(nonEmpty, id);
        return mapSortingLogEntries(filteredLogEntries);
    }

    private List<LogEntry> filterBlackList(List<LogEntry> logEntries, String id) {
        System.out.printf("Thread %s started filtering %s%n", Thread.currentThread().getName(), id);
        return filterService.applyAndGetFilteredEntries(Constants.BLACKLIST_URL, logEntries);
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
}