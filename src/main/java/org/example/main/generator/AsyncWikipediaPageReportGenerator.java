package org.example.main.generator;

import org.example.main.connectivity.WikipediaPageViewConnectService;
import org.example.main.filter.FilterService;
import org.example.main.model.LogEntry;
import org.example.main.storage.StorageService;
import org.example.main.util.FileUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncWikipediaPageReportGenerator extends PageViewReportGenerator {

    private final StorageService<Set<LogEntry>, Path> storageService;
    private final ExecutorService executor;
    private static final int MAX_THREAD_COUNT = 3;

    public AsyncWikipediaPageReportGenerator(FilterService<LogEntry> filterService, WikipediaPageViewConnectService connectService, StorageService<Set<LogEntry>, Path> fileStorage, int threshold) {
        super(connectService, filterService, threshold);
        this.storageService = fileStorage;
        this.executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    }

    @Override
    protected void saveToDevice(Tuple2<Set<LogEntry>, Path> tuple2) {
        CompletableFuture.runAsync(() -> saveToDevicePipeline(tuple2), executor);
    }

    private CompletableFuture<Tuple2<Set<LogEntry>, Path>> mapSortedLogEntriesPipeline(Tuple2<String, Path> tuple) {
        return CompletableFuture.supplyAsync(() -> {
            String url = tuple.getT1();
            Path filePath = tuple.getT2();
            boolean missedFile = FileUtils.fileNotExists(filePath);
            if (!missedFile) {
                System.out.printf("Skipped processing request %s while file exists%n.", filePath);
                return null;
            }
            Set<LogEntry> logEntries = fetchWikiPageViews(url);
            return Tuples.of(logEntries, tuple.getT2());
        }, executor);
    }

    public void saveToDevicePipeline(Tuple2<Set<LogEntry>, Path> logEntriesPathTuple) {
        if (logEntriesPathTuple == null) {
            return;
        }
        var logEntries = logEntriesPathTuple.getT1();
        var filePath = logEntriesPathTuple.getT2();
        System.out.printf("writing %d records in path: %s%n", logEntries.size(), filePath);
        storageService.write(logEntries, filePath);
    }

    public void exit() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
    }

    @Override
    protected Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple) {
        return this.mapSortedLogEntriesPipeline(tuple).join();
    }
}