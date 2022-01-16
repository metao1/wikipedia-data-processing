package com.top.wiki.filter;

import com.top.wiki.connectivity.BlackListConnectivityService;
import com.top.wiki.model.LogEntry;
import com.top.wiki.util.LogEntryUtils;
import com.top.wiki.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BlackListFilter service class
 */
public class BlackListFilterService implements FilterService<LogEntry> {

    // an interface to which the  blacklist server is located
    private final BlackListConnectivityService blackListService;

    public BlackListFilterService(BlackListConnectivityService blackListService) {
        this.blackListService = blackListService;
    }

    @Override
    public List<LogEntry> applyAndGetFilteredEntries(String blackListUrl, final List<LogEntry> logEntries) {
        List<String> blackList = fetchBlackList(blackListUrl);
        List<LogEntry> filteredLogEntries = getBlackListFilteredLogEntries(blackList, logEntries);
        System.out.printf("Filtered %d items out of %d items%n", logEntries.size() - filteredLogEntries.size(), logEntries.size());
        return filteredLogEntries;
    }

    /**
     * A helper method to simply connects to blacklist server and fetches the content in string format as list
     * of entries in simple String format separated by lines.
     * @param blackListUrl the http address of remote blacklist server
     * @return list of entries in simple String format separated by lines.
     */
    public List<String> fetchBlackList(String blackListUrl) {
        return blackListService.fetchBlacklist(blackListUrl);
    }

    /**
     * A helper filter function to check in parallel if any logentry is NOT presented in blacklist,
     * It creates a Hash Set of black list to search from by key as page_title + " " + page_count_view
     * then stores compare them with logentries and returns a filtered list of logentries
     * @param blackList as lists of blacklist items
     * @param logEntries in which need to be checked
     * @return filtered unsorted in order list of logentries
     */
    private List<LogEntry> getBlackListFilteredLogEntries(List<String> blackList, List<LogEntry> logEntries) {
        var map = buildMapFromBlackList(blackList);
        return logEntries
                .parallelStream()
                .filter(item -> missedInBlacklist(map, item))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Build a map from black list to help increase searching in blacklist with O(1)
     * @param blackList the original blacklist items as string
     * @return set of mapped blacklist items by key as by page_title + " " + page_count_view
     */
    private Set<String> buildMapFromBlackList(List<String> blackList) {
        return blackList
                .parallelStream()
                .map(line -> {
                    try {
                        return LogEntryUtils.parseLineBlacklistLogEntry(line);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("warning: cannot parse one line of blacklist input");
                    }
                    return LogEntry.empty();
                })
                .filter(le -> le != null && StringUtils.notBlank(le.getDomainCode()))
                .map(LogEntry::getDomainCodeAndPageTitle)
                .collect(Collectors.toSet());
    }

    /**
     * check if the LogEntry exists in blacklist already
     * @param blacklistSet as repository to check against
     * @param entry the items to check
     * @return true if exists
     */
    private static boolean missedInBlacklist(Set<String> blacklistSet, LogEntry entry) {
        return entry != null && !blacklistSet.contains(entry.getDomainCodeAndPageTitle());
    }
}
