package org.example.main.generator;

import org.example.main.connectivity.WikipediaPageViewConnectService;
import org.example.main.filter.FilterService;
import org.example.main.model.LogEntry;
import org.example.main.storage.StorageService;
import org.example.main.util.Constants;
import org.example.main.util.FileUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncWikipediaPageReportGenerator extends PageViewReportGenerator {

    private final StorageService<Set<LogEntry>, Path> storageService;
    private final ExecutorService ioExecutor;
    private final ExecutorService executor;

    public AsyncWikipediaPageReportGenerator(FilterService<LogEntry> filterService, WikipediaPageViewConnectService connectService, StorageService<Set<LogEntry>, Path> fileStorage, int threshold) {
        super(connectService, filterService, threshold);
        this.storageService = fileStorage;
        this.ioExecutor = Executors.newFixedThreadPool(Constants.MAX_IO_THREAD_COUNT);
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    protected void saveToDevice(Tuple2<Set<LogEntry>, Path> tuple2) {
        CompletableFuture.runAsync(() -> saveToDevicePipeline(tuple2), executor);
    }

    private CompletableFuture<Tuple2<Set<LogEntry>, Path>> mapSortedLogEntriesPipeline(Tuple2<String, Path> tuple2) {
        return CompletableFuture.supplyAsync(() -> {
                    String url = tuple2.getT1();
                    Path filePath = tuple2.getT2();
                    boolean missedFile = FileUtils.fileNotExists(filePath);
                    if (!missedFile) {
                        System.out.printf("Skipped processing request %s while file exists.%n", filePath);
                        return null;
                    }
                    return Tuples.of(tuple2.getT1(), tuple2.getT2(), fetchWikiPageViews(url));
                }, ioExecutor)
                .thenComposeAsync((tuple3) -> CompletableFuture.supplyAsync(() -> {
                    List<LogEntry> filteredLogEntries = filterBlackList(tuple3.getT3(), tuple3.getT1());
                    return Tuples.of(tuple3.getT1(), tuple3.getT2(), filteredLogEntries);
                }), executor)
                .thenComposeAsync(tuple3 -> CompletableFuture.supplyAsync(() -> {
                    Set<LogEntry> logEntries = mapSortingLogEntries(tuple3.getT3());
                    return Tuples.of(logEntries, tuple3.getT2());
                }), executor)
                .handle((res, err) -> {
                    if (err != null) {
                        System.err.println(err.getMessage());
                    }
                    return res;
                });
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

    public void exit() {
        exitExecutor(ioExecutor);
        exitExecutor(executor);
    }

    private void exitExecutor(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple) {
        return this.mapSortedLogEntriesPipeline(tuple).join();
    }
}