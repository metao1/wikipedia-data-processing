package com.top.wiki.filter;

import com.top.wiki.connectivity.BlacklistConnection;
import com.top.wiki.model.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * BlackListFilter service class
 */
@Slf4j
@RequiredArgsConstructor
public class BlackListFilterService implements FilterService<LogEntry> {

    // an interface to which the blacklist server is located
    private final BlacklistConnection blackListConnection;

    @Override
    public void applyFilteredEntries(String blackListUrl, final List<LogEntry> logEntries) {
        List<String> blackList = blackListConnection.fetchBlacklist(blackListUrl);
        List<LogEntry> filteredLogEntries = logEntries
                .stream()
                .filter(item -> missedInBlacklist(blackList, item))
                .toList();
        log.info("Filtered {} items out of {} items", logEntries.size() - filteredLogEntries.size(), logEntries.size());
    }

    /**
     * check if the LogEntry exists in blacklist already
     *
     * @param blacklistSet as repository to check against
     * @param entry        the items to check
     * @return true if exists
     */
    private static boolean missedInBlacklist(List<String> blacklistSet, LogEntry entry) {
        return entry != null && !blacklistSet.contains(entry.getDomainCodeAndPageTitle());
    }
}
