package com.top.wiki;

import com.top.wiki.model.LogEntry;
import com.top.wiki.storage.WikiPageViewFileOperator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WikiPageViewFileOperatorTest {

    @Test
    public void givenLogEntries_saveIntoDisk_thenIsOk() throws IOException {
        var storageService = new WikiPageViewFileOperator();
        var logEntries = Set.of(new LogEntry("en.s", "test", 1));
        var tempFile = File.createTempFile("WikiPageViewFileOperatorTest", null);
        assertTrue(tempFile.exists());
        boolean success = storageService.write(logEntries, tempFile.toPath());
        assertTrue(success);
        var str = Files.readString(tempFile.toPath());
        assertNotNull(str);
        assertEquals("en.s\ttest\t1\n", str);
    }
}
