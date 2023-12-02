package com.top.wiki.model;

import lombok.Value;

import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds the max page views of the current logentries and the current logentries itself
 */
@Value
public class PageViewItem {
    AtomicInteger maxEntries;
    SortedSet<LogEntry> logEntries;

    public PageViewItem(Integer initialCountedView, SortedSet<LogEntry> logEntries) {
        this.maxEntries = new AtomicInteger(initialCountedView);
        this.logEntries = logEntries;
    }

    /**
     * Finds min logentry among all logentries
     *
     * @return min logentry
     */
    public LogEntry minLogEntryPageView() {
        return logEntries.first();
    }

    public void addNewItem(LogEntry le) {
        logEntries.add(le);
    }

    /**
     * Atomicity increment the max entry count
     */
    public void updateMaxEntryCount() {
        maxEntries.incrementAndGet();
    }
}
