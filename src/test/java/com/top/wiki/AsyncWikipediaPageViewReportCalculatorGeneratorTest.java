package com.top.wiki;

import com.top.wiki.connectivity.BlacklistConnection;
import com.top.wiki.connectivity.HttpConnection;
import com.top.wiki.connectivity.PageViewConnector;
import com.top.wiki.connectivity.StandardHttp;
import com.top.wiki.filter.BlackListFilterService;
import com.top.wiki.generator.AsyncPageViewReportGenerator;
import com.top.wiki.model.LogEntry;
import com.top.wiki.storage.WikiPageViewFileOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AsyncWikipediaPageViewReportCalculatorGeneratorTest {

    HttpConnection httpConnection = Mockito.mock(StandardHttp.class);

    @Test
    @SneakyThrows
    void getSortedSet_theIsOk() {
        var inputStream = getClass().getClassLoader().getResourceAsStream("blacklist.txt");
        var blackListConnection = new BlacklistConnection(httpConnection);
        var wikipediaPageViewConnection = new PageViewConnector(httpConnection);
        when(httpConnection.read(anyString())).thenReturn(inputStream);

        var blacklistService = new BlackListFilterService(blackListConnection);
        var logEntries = new LinkedList<LogEntry>();
        for (int i = 0; i < 10; i++) {
            logEntries.add(new LogEntry("en" + i, "head" + i, i));
        }
        //shuffle the list to make it unsorted
        Collections.shuffle(logEntries);

        assertFalse(isSorted(logEntries, Comparator.naturalOrder()));

        var storageService = new WikiPageViewFileOperator();
        var reportGenerator = new AsyncPageViewReportGenerator(blacklistService, wikipediaPageViewConnection, storageService, 25);
        var sortedLogEntries = reportGenerator.calculateSortedSetLogEntries(logEntries);
        assertNotNull(sortedLogEntries);

        // we expect the set to be sorted
        assertTrue(isSorted(sortedLogEntries, Comparator.naturalOrder()));
    }

    private static boolean isSorted(Collection<LogEntry> logEntries, Comparator<LogEntry> logEntryComparator) {
        if (logEntries == null || logEntries.size() == 1) {
            return true;
        }

        Iterator<LogEntry> iter = logEntries.iterator();
        LogEntry current, previous = iter.next();
        while (iter.hasNext()) {
            current = iter.next();
            if (logEntryComparator.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
}
