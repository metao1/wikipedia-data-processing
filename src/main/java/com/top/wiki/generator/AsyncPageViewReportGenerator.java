package com.top.wiki.generator;

import com.top.wiki.connectivity.PageViewConnector;
import com.top.wiki.filter.FilterService;
import com.top.wiki.model.LogEntry;
import com.top.wiki.storage.StorageService;
import com.top.wiki.util.Constants;
import com.top.wiki.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AsyncPageViewReportGenerator extends PageViewReportGenerator {

    private final StorageService<Set<LogEntry>, Path> storageService;
    private final ExecutorService ioExecutor;
    private final ExecutorService executor;

    public AsyncPageViewReportGenerator(FilterService<LogEntry> filterService, PageViewConnector connectService, StorageService<Set<LogEntry>, Path> fileStorage, int threshold) {
        super(connectService, filterService, threshold);
        this.storageService = fileStorage;
        //we must limit the connections number in i/o executors.
        this.ioExecutor = Executors.newFixedThreadPool(Constants.MAX_IO_THREAD_COUNT);
        //simple fork/join threads
        this.executor = Executors.newFixedThreadPool(Constants.MAX_IO_THREAD_COUNT);
    }

    /**
     * saves the logentries into device asynchronously (we don't wait for operation
     * and it will be happening in background);
     *
     * @param tuple2 pair of logentries set and path to save them
     */
    @Override
    protected void saveToDevice(Tuple2<Set<LogEntry>, Path> tuple2) {
        //Saves to disk asynchronously fashion
        CompletableFuture.runAsync(() -> saveToDevicePipeline(tuple2), executor);
    }

    /**
     * saves the result into device
     *
     * @param logEntriesPathTuple paired set of logentries and path to which they should be saved
     */
    public void saveToDevicePipeline(Tuple2<Set<LogEntry>, Path> logEntriesPathTuple) {
        if (logEntriesPathTuple == null) {
            return;
        }
        var logEntries = logEntriesPathTuple.getT1();
        var filePath = logEntriesPathTuple.getT2();
        log.info("writing {} records in path: {}", logEntries.size(), filePath);
        //write to storage service
        boolean wrote = storageService.write(logEntries, filePath);
        if (wrote) {
            log.info("wrote {} records in path: {} successfully.", logEntries.size(), filePath);
        } else {
            log.error("could not wrote {} records to path: {} operation not permitted..", logEntries.size(), filePath);
        }
    }

    /**
     * A set of pipelines running as chain of operations called phase. Each phase calculates set of group of
     * operations and then sends the output to the next chain. The chains define the business logics
     * while keeping the implementations away from the business.
     *
     * @param tuple as a pair of URL to the PageView in wikipedia host and Path to where to store it
     * @return a CompletableFuture that defines pair set of logentries and path in which we have given as input param
     */
    private CompletableFuture<Tuple2<Set<LogEntry>, Path>> startPipeline(Tuple2<String, Path> tuple) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return getLogEntriesPhase(tuple);//first get logEntries
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }, executor)
                .thenComposeAsync(tuple3 -> {//then filter them
                    return CompletableFuture.supplyAsync(() -> filteredLogEntriesPipeline(tuple3));
                }, ioExecutor)
                .thenComposeAsync(tuple3 -> CompletableFuture.supplyAsync(() -> {// then calculate the highest pageviews
                    // from logentries
                    return calculateSortedSetLogEntriesPipeline(tuple3);
                }), executor);
    }

    private Tuple2<Set<LogEntry>, Path> calculateSortedSetLogEntriesPipeline(Tuple3<String, Path, List<LogEntry>> tuple3) {
        if (tuple3 == null) {
            return null;
        }
        Set<LogEntry> logEntries = calculateSortedSetLogEntries(tuple3.getT3());
        return Tuples.of(logEntries, tuple3.getT2());
    }

    private Tuple3<String, Path, List<LogEntry>> filteredLogEntriesPipeline(Tuple3<String, Path, List<LogEntry>> tuple3) {
        if (tuple3 == null) {
            return null;
        }
        filterBlackList(tuple3.getT3(), tuple3.getT1());
        return Tuples.of(tuple3.getT1(), tuple3.getT2(), tuple3.getT3());
    }

    private Tuple3<String, Path, List<LogEntry>> getLogEntriesPhase(Tuple2<String, Path> tuple2) throws MalformedURLException {
        if (tuple2 == null) {
            return null;
        }
        String url = tuple2.getT1();
        Path filePath = tuple2.getT2();
        boolean missedFile = FileUtils.fileNotExists(filePath);
        if (!missedFile) {
            log.error("Skipped processing request {} while file exists.", filePath);
            return null;
        }
        List<LogEntry> logEntries = fetchWikiPageViews(url);
        return Tuples.of(tuple2.getT1(), tuple2.getT2(), logEntries);
    }

    /**
     * Stops all executors and stop them from getting new requests
     */
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
                    log.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Starts pipeline and wait for operation to finish in background
     *
     * @param tuple is paired of URL to get the wikipedia page from and save it as output into desired path
     * @return the pair of sorted logentries and path to save them
     */
    @Override
    protected Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple) {
        return startPipeline(tuple)
                .handle((res, err) -> {
                    if (err != null) {
                        log.error(err.getMessage());
                        return null;
                    }
                    return res;
                }).join();
    }
}