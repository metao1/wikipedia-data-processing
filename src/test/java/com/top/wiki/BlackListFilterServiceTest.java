package com.top.wiki;

import com.top.wiki.connectivity.MockedBasicInputService;
import com.top.wiki.connectivity.WikiPagesBlacklistConnectionService;
import com.top.wiki.filter.BlackListFilterService;
import com.top.wiki.model.LogEntry;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.LinkedList;

public class BlackListFilterServiceTest {

    @Test
    public void givenUrl_getBlackListFilteredLogEntries_isSuccess() {
        var connectionService = new MockedBasicInputService();
        var path = Paths.get("src", "test", "resources", "blacklist.txt");
        var blacklistService = new BlackListFilterService(new WikiPagesBlacklistConnectionService(connectionService));
        var logEntries = new LinkedList<LogEntry>();
        for (int i = 0; i < 10; i++) {
            logEntries.add(new LogEntry("en" + i, "head" + i, i));
        }
        blacklistService.applyAndGetFilteredEntries(path.toFile().getAbsolutePath(), logEntries);

    }
}
