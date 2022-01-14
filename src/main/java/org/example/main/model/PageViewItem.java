package org.example.main.model;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

public class PageViewItem {
    private final AtomicInteger maxEntries;
    private final SortedSet<LogEntry> logEntries;

    public PageViewItem(Integer initialCountedView, SortedSet<LogEntry> logEntries) {
        this.maxEntries = new AtomicInteger(initialCountedView);
        this.logEntries = logEntries;
    }

    public LogEntry minLogEntryPageView() {
        return logEntries.stream().min(Comparator.naturalOrder()).orElse(null);
    }

    public void addNewItem(LogEntry le) {
        logEntries.add(le);
    }

    public void removeLogEntry(LogEntry le) {
        logEntries.remove(le);
    }

    public Integer getMaxEntryCount() {
        return maxEntries.get();
    }

    public SortedSet<LogEntry> getLogEntries() {
        return logEntries;
    }

    public void updateMaxEntryCount() {
        maxEntries.incrementAndGet();
    }
}
