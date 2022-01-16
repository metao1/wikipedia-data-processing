package com.top.wiki;

import com.top.wiki.connectivity.MockedBasicInputService;
import com.top.wiki.connectivity.WikiPagesBlacklistConnectionService;
import com.top.wiki.connectivity.WikipediaPageViewConnectService;
import com.top.wiki.filter.BlackListFilterService;
import com.top.wiki.generator.AsyncWikipediaPageViewReportGenerator;
import com.top.wiki.model.LogEntry;
import com.top.wiki.storage.WikiPageViewFileOperator;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AsyncWikipediaPageViewReportGeneratorTest {

    @Test
    public void getSortedSet_theIsOk() {
        var connectionService = new MockedBasicInputService();
        var path = Paths.get("src", "test", "resources", "blacklist.txt");
        var blackListConnection = new WikiPagesBlacklistConnectionService(connectionService);
        var wikipediaPageViewConnection = new WikipediaPageViewConnectService(connectionService);
        var blacklistService = new BlackListFilterService(blackListConnection);
        var logEntries = new LinkedList<LogEntry>();
        for (int i = 0; i < 10; i++) {
            logEntries.add(new LogEntry("en" + i, "head" + i, i));
        }
        //shuffle the list to make it unsorted
        Collections.shuffle(logEntries);

        assertFalse(isSorted(logEntries, Comparator.naturalOrder()));

        var storageService = new WikiPageViewFileOperator();
        var reportGenerator = new AsyncWikipediaPageViewReportGenerator(blacklistService, wikipediaPageViewConnection, storageService, 25);
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
