package com.top.wiki.connectivity;

import com.top.wiki.model.LogEntry;
import com.top.wiki.util.WikiStringUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PageViewConnectorTest {

    HttpConnection httpConnection = Mockito.mock(GzipHttp.class);

    PageViewConnector connector = new PageViewConnector(httpConnection);

    @Test
    @SneakyThrows
    void fetchLogEntries() {
        String pageViewUrl = "https://dumps.wikimedia.org/other/pageviews/2023/2023-08/pageviews-20230801-000000.gz";
        InputStream providedInputStream = getClass().getClassLoader().getResourceAsStream("test.gz");
        when(httpConnection.read(anyString())).thenReturn(providedInputStream);
        PageViewConnector retrievedLogEntries = connector.fetchLogEntries(pageViewUrl);

        List<LogEntry> logEntries = retrievedLogEntries.map(LogEntry::mapToLogEntry);

        assertThat(logEntries)
                .hasSize(216);
    }
}