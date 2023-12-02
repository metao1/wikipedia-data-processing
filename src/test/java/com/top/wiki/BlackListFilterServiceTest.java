package com.top.wiki;

import com.top.wiki.connectivity.BlacklistConnection;
import com.top.wiki.connectivity.HttpConnection;
import com.top.wiki.connectivity.PageViewConnector;
import com.top.wiki.connectivity.StandardHttp;
import com.top.wiki.filter.BlackListFilterService;
import com.top.wiki.model.LogEntry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BlackListFilterServiceTest {

    HttpConnection httpConnection = Mockito.mock(StandardHttp.class);

    @Test
    @SneakyThrows
    void givenUrl_getBlackListFilteredLogEntries_isSuccess() {
        var inputStream = getClass().getClassLoader().getResourceAsStream("blacklists.txt");
        when(httpConnection.read(anyString())).thenReturn(inputStream);

        var blacklistService = new BlackListFilterService(new BlacklistConnection(httpConnection));
        var logEntries = new LinkedList<LogEntry>();
        for (int i = 0; i < 10; i++) {
            logEntries.add(new LogEntry("en" + i, "head" + i, i));
        }
        blacklistService.applyFilteredEntries(getClass().getClassLoader().getName(), logEntries);

    }
}
