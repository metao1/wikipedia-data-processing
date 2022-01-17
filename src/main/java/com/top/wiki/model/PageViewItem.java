package com.top.wiki.model;

import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds the max page views of the current logentries and the current logentries itself
 */
public class PageViewItem {
    private final AtomicInteger maxEntries;
    private final SortedSet<LogEntry> logEntries;

    public PageViewItem(Integer initialCountedView, SortedSet<LogEntry> logEntries) {
        this.maxEntries = new AtomicInteger(initialCountedView);
        this.logEntries = logEntries;
    }

    /**
     * Finds min logentry among all logentries
     * @return min logentry
     */
    public LogEntry minLogEntryPageView() {
        return logEntries.first();
    }

    public void addNewItem(LogEntry le) {
        logEntries.add(le);
    }

    public void removeLogEntry(LogEntry le) {
        logEntries.remove(le);
    }

    /**
     * Gets number of max entries
     * @return max entries
     */
    public Integer getMaxEntryCount() {
        return maxEntries.get();
    }

    public SortedSet<LogEntry> getLogEntries() {
        return logEntries;
    }

    /**
     * Atomicity increment the max entry count
     */
    public void updateMaxEntryCount() {
        maxEntries.incrementAndGet();
    }
}
