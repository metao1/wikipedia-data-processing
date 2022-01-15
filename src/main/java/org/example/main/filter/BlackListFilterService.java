package org.example.main.filter;

import org.example.main.connectivity.BlackListConnectivityService;
import org.example.main.model.LogEntry;
import org.example.main.util.LogEntryUtils;
import org.example.main.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlackListFilterService implements FilterService<LogEntry> {

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

    public List<String> fetchBlackList(String blackListUrl) {
        return blackListService.fetchBlacklist(blackListUrl);
    }

    private List<LogEntry> getBlackListFilteredLogEntries(List<String> blackList, List<LogEntry> logEntries) {
        List<LogEntry> result = new LinkedList<>();
        var map = buildMapFromBlackList(blackList);
        return logEntries
                .stream()
                .filter(f -> map.contains(f.getDomainCodeAndPageTitle()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Set<String> buildMapFromBlackList(List<String> blackList) {
        return Flux.fromStream(blackList.stream())
                .map(LogEntryUtils::parseLineBlacklistLogEntry)
                .onErrorReturn(new LogEntry("", "", 0))
                .filter(le -> StringUtils.notBlank(le.getDomainCode()))
                .map(LogEntry::getDomainCodeAndPageTitle)
                .distinct(Function.identity())
                .collect(Collectors.toSet())
                .block();
    }

    private static boolean containsInBlacklist(Set<String> blacklistSet, String item) {
        return blacklistSet.contains(item);
    }
}
