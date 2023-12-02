package com.top.wiki;

import com.top.wiki.model.LogEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class LogEntryUtilsTest {

    @Test
    void givenStringLine_parseLineBlacklistLogEntry_isSuccess() {
        var line = "en head";
        var logEntry = LogEntry.buildLogEntry(line);
        assertNotNull(logEntry);
        assertEquals("en", logEntry.getDomainCode());
        assertEquals("head", logEntry.getPageTitle());
    }

    @Test
    void givenStringLine_parseLineBlacklistLogEntry_isFailure() {
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> {
            var line = "en";
            LogEntry.buildLogEntry(line);
        });
    }

}
