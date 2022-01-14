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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static org.example.main.util.Constants.WIKIPEDIA_DIR;

public class WikipediaPageReportGenerator {

    private final WikipediaPageViewConnectService wikiOperator;
    private final FilterService<LogEntry> filterService;
    private final FileStorage<Set<LogEntry>, Path> fileStorage;
    private final int threshold;

    public WikipediaPageReportGenerator(FilterService<LogEntry> filterService, WikipediaPageViewConnectService wikiOperator, FileStorage<Set<LogEntry>, Path> fileStorage, int threshold) {
        this.filterService = filterService;
        this.wikiOperator = wikiOperator;
        this.fileStorage = fileStorage;
        this.threshold = threshold;
    }

    public void execute(List<LocalDateTime> timeList) {
        Flux.fromStream(timeList.stream())
                .map(dateTime -> wikiOperator.buildPageViewUriEntry(dateTime, WIKIPEDIA_DIR))
                .doOnNext(this::checkDirectory)
                .delayElements(Duration.ofSeconds(10))
                .flatMap(this::generateLogEntriesPipeline)
                .subscribe(this::saveToDevicePipeline);
    }

    private void saveToDevicePipeline(Tuple2<Path, Set<LogEntry>> tuple2) {
//        var copyName = Thread.currentThread().getName() + "file-copy";
//        var logEntries = tuple2.getT2();
//        var filePath = tuple2.getT1();
//        System.out.printf("writing %d items in parallel in path: %s%n", logEntries.size(), filePath);
//        Flux<LogEntry> logEntryFlux = Flux.using(logEntries::stream
//                        , Flux::fromStream
//                        , Stream::close
//                )
//                .subscribeOn(Schedulers.newParallel(copyName, 10))
//                .share();
        var logEntries = tuple2.getT2();
        var filePath = tuple2.getT1();
        System.out.printf("writing %d records in path: %s%n", logEntries.size(), filePath);
        fileStorage.write(logEntries,filePath);
    }

    private Mono<Set<LogEntry>> generateLogEntriesPipeline(String path) {
        return Mono
                .fromCallable(() -> fetchWikiPageViews(path))
                .delayElement(Duration.ofSeconds(10))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void checkDirectory(Tuple2<Path, String> entry) {
        var out = entry.getT1();
        if (FileUtils.fileNotExists(out)) {
            try {
                Files.createDirectories(out.getParent());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("skipping processing since file " + out.getFileName() + " already exists");
        }
    }

    public Set<LogEntry> fetchWikiPageViews(final String url) {
        var id = StringUtils.extractIdentification(url);
        List<LogEntry> logEntries = CollectionUtils.nonEmptyListConvertor(wikiOperator.fetchLogEntries(url));
        List<LogEntry> filteredLogEntries = filterBlackList(logEntries, id);
        return CollectionUtils.convertToSet(filteredLogEntries);
    }

    private List<LogEntry> filterBlackList(List<LogEntry> logEntries, String id) {
        System.out.printf("Thread %s started filtering %s%n", Thread.currentThread().getName(), id);
        return filterService.applyAndGetFilteredEntries(Constants.BLACKLIST_URL, logEntries);
    }

    public Flux<Set<LogEntry>> mapSortingLogEntries(List<LogEntry> logEntries) {
        ConcurrentMap<String, PageViewItem> sortedSetMap = new ConcurrentHashMap<>();
        int processedEntriesNum = calcUpdateTopPageViewsMap(logEntries, sortedSetMap);
        System.out.printf("Processed %d entries%n in total.", processedEntriesNum);
        return Flux.fromStream(sortedSetMap.values()
                .stream()
                .flatMap(e -> Stream.of(e.getLogEntries())));
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

    private void reportError(Throwable err) {
        System.err.println("error while fetching the wikiPageViews:" + err.getMessage());
    }
}