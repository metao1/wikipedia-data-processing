package com.top.wiki.generator;

import com.top.wiki.connectivity.WikipediaPageViewConnectService;
import com.top.wiki.filter.FilterService;
import com.top.wiki.model.LogEntry;
import com.top.wiki.storage.StorageService;
import com.top.wiki.util.Constants;
import com.top.wiki.util.FileUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Makes a wrapper over the PageViewReportGenerator abstract class that uses async operations
 * to exploit speed of parallel operations, facilitates many threads to run in parallel.
 * This class defines the overall actions that needs to create Wikipedia Page Views.
 * create set of operation chains in which some can be dependent on some other
 * defines phases for which each phase can be run separately or waiting for previous
 * phase to get the response from and starting operation.
 * This class uses executors to run thread in thread-pools.
 * Each phase sends the result to other phase respectively.
 * The chain won't block since this will affect performance of
 * the whole application, but instead it uses some functionalities
 * to send the threads in I/O operations in background in waiting mode.
 * This helps the other thread to still run and get the change to run.
 * As wikipedia remote host won't allow more than 3 concurrent open connection,
 * we must limit the connections number in executors.
 */

public class AsyncWikipediaPageViewReportGenerator extends PageViewReportGenerator {

    private final StorageService<Set<LogEntry>, Path> storageService;
    private final ExecutorService ioExecutor;
    private final ExecutorService executor;

    public AsyncWikipediaPageViewReportGenerator(FilterService<LogEntry> filterService, WikipediaPageViewConnectService connectService, StorageService<Set<LogEntry>, Path> fileStorage, int threshold) {
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
        System.out.printf("writing %d records in path: %s%n", logEntries.size(), filePath);
        //write to storage service
        boolean wrote = storageService.write(logEntries, filePath);
        if (wrote) {
            System.out.printf("wrote %d records in path: %s successfully.%n", logEntries.size(), filePath);
        } else {
            System.err.printf("could not wrote %d records to path: %s operation not permitted..%n", logEntries.size(), filePath);
        }
    }

    /**
     * A set of pipelines running as chain of operations called phase. Each phase calculates set of group of
     * operations and then sends the output to the next chain. The chains define the business logics
     * while keeping the implementations away from the business.
     *
     * @param tuple as pair of URL to the PageView in wikipedia host and Path to where to store it
     * @return a CompletableFuture that defines pair set of logentries and path in which we have given as input param
     */
    private CompletableFuture<Tuple2<Set<LogEntry>, Path>> startPipeline(Tuple2<String, Path> tuple) {
        return CompletableFuture.supplyAsync(() -> {
                    return getLogEntriesPhase(tuple);//first get logentries
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
        List<LogEntry> filteredLogEntries = filterBlackList(tuple3.getT3(), tuple3.getT1());
        return Tuples.of(tuple3.getT1(), tuple3.getT2(), filteredLogEntries);
    }

    private Tuple3<String, Path, List<LogEntry>> getLogEntriesPhase(Tuple2<String, Path> tuple2) {
        if (tuple2 == null) {
            return null;
        }
        String url = tuple2.getT1();
        Path filePath = tuple2.getT2();
        boolean missedFile = FileUtils.fileNotExists(filePath);
        if (!missedFile) {
            System.out.printf("Skipped processing request %s while file exists.%n", filePath);
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
                    System.err.println("Pool did not terminate");
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
     * @return he pair of sorted logentries and path to save them
     */
    @Override
    protected Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple) {
        return startPipeline(tuple)
                .handle((res, err) -> {
                    if (err != null) {
                        System.err.println(err.getMessage());
                        return null;
                    }
                    return res;
                })
                .join();
    }
}