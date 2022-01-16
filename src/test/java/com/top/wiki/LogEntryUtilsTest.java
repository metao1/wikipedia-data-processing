package com.top.wiki;

import com.top.wiki.util.LogEntryUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogEntryUtilsTest {

    @Test
    public void givenStringLine_parseLineBlacklistLogEntry_isSuccess() {
        var line = "en head";
        var logEntry = LogEntryUtils.parseLineBlacklistLogEntry(line);
        assertNotNull(logEntry);
        assertEquals("en", logEntry.getDomainCode());
        assertEquals("head", logEntry.getPageTitle());
    }

    @Test
    public void givenStringLine_parseLineBlacklistLogEntry_isFailure() {
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> {
            var line = "en";
            LogEntryUtils.parseLineBlacklistLogEntry(line);
        });
    }

}
