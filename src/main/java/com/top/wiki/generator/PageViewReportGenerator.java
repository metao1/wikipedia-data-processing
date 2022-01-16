package com.top.wiki.generator;

import com.top.wiki.connectivity.WikipediaPageViewConnectService;
import com.top.wiki.filter.FilterService;
import com.top.wiki.model.LogEntry;
import com.top.wiki.model.PageViewItem;
import com.top.wiki.util.Constants;
import com.top.wiki.util.FileUtils;
import com.top.wiki.util.LogEntryUtils;
import com.top.wiki.util.StringUtils;
import reactor.util.function.Tuple2;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An abstract class that defines the reporting procedure for Top Page View visitors of Wikipedia
 */
public abstract class PageViewReportGenerator {
    protected final WikipediaPageViewConnectService wikiConnector;
    protected final FilterService<LogEntry> filterService;
    protected final int threshold;

    public PageViewReportGenerator(WikipediaPageViewConnectService connectService, FilterService<LogEntry> filterService, int threshold) {
        this.filterService = filterService;
        this.wikiConnector = connectService;
        this.threshold = threshold;
    }

    /**
     * Given time list as input save the desire output into files
     * and return list of affected locations
     *
     * @param timeList the desire period time list
     * @return effected locations in where result saved in.
     */
    public List<Path> execute(List<LocalDateTime> timeList) {
        checkIfWikiPageViewDirExist();
        List<Path> collect = timeList.stream()
                .map(LogEntryUtils::urlToPathTuple)
                .map(this::getSortedEntries)
                .peek(this::saveToDevice)
                .filter(Objects::nonNull)
                .map(Tuple2::getT2)
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * Abstract method to let save into device.
     *
     * @param entries a paired of log entry set and path in which they save.
     */
    protected abstract void saveToDevice(Tuple2<Set<LogEntry>, Path> entries);

    /**
     * Exit operation when execute() method done working. This method must triggered, explicitly or implicitly.
     *
     * @throws InterruptedException when can't stop running application
     */
    protected abstract void exit() throws InterruptedException;

    /**
     * Checks if the result directory existed, otherwise create this directory explicitly
     */
    private void checkIfWikiPageViewDirExist() {
        FileUtils.checkDirectory(Path.of(Constants.WIKIPEDIA_PAGE_VIEW_DIR, ":"));
    }

    /**
     * Get tuple as paired and sort them out based-on LogEntry object compare function definition
     *
     * @param tuple is paired of URL to get the wikipedia page from and save it as output into desired path
     * @return Pairs of LogEntry Set and Path where these should be saved as result
     */
    protected abstract Tuple2<Set<LogEntry>, Path> getSortedEntries(Tuple2<String, Path> tuple);

    /**
     * Simple proxy method to connect to network and returns parsed to a list of LogEntry
     *
     * @param url using connect to network
     * @return parsed list of LogEntry
     */
    public List<LogEntry> fetchWikiPageViews(final String url) {
        return wikiConnector.fetchLogEntries(url);
    }

    /**
     * Process log entry list and calculate and only return top view pages
     *
     * @param logEntries unsorted log entry list to process
     * @return sorted set of log entry objects
     */
    public Set<LogEntry> calculateSortedSetLogEntries(List<LogEntry> logEntries) {
        Map<String, PageViewItem> sortedSetMap = new HashMap<>();
        int processedEntriesNum = calcUpdateTopPageViews(logEntries, sortedSetMap);
        System.out.printf("Processed %d entries in total.%n", processedEntriesNum);
        // while adding to sorted set it will be sorted. look at compare() function in LogEntry to see the logic
        Set<LogEntry> sortedSet = new TreeSet<>();
        for (PageViewItem pvi : sortedSetMap.values()) {
            sortedSet.addAll(pvi.getLogEntries());
        }
        return sortedSet;
    }

    /**
     * Helper method that calculates Top Page Views and updates the map to keep track of PageViews
     *
     * @param logEntries the log entry list which is unsorted
     * @param map        that will be updated as reference
     * @return the number of operated items
     */
    private int calcUpdateTopPageViews(List<LogEntry> logEntries, Map<String, PageViewItem> map) {
        var i = 0;
        for (LogEntry le : logEntries) {
            var dc = le.getDomainCode();
            // checks if the domain code already presents in map
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
                //if domain code missed just create new i
                var set = new TreeSet<LogEntry>();
                set.add(le);
                var nle = new PageViewItem(1, set);
                map.put(dc, nle);
            }
            if (i++ % 1000000 == 0) System.out.printf("Processed %d entries%n", i);
        }
        return i;
    }

    /**
     * Filters LogEntry items apply filtering on those LogEntries that exists on blacklist
     * The response value calculates by giving the url of blacklist of remote host that host
     * the blacklist items.
     *
     * @param logEntries unfiltered logentries to filter
     * @param url        to calculate the identification for logging
     * @return the filtered logentries as unsorted list
     */
    public List<LogEntry> filterBlackList(List<LogEntry> logEntries, String url) {
        var id = StringUtils.extractIdentification(url);
        System.out.printf("Thread %s started filtering %s%n", Thread.currentThread().getName(), id);
        return filterService.applyAndGetFilteredEntries(Constants.BLACKLIST_URL, logEntries);
    }


}
