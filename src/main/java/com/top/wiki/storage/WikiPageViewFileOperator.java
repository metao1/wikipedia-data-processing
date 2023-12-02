package com.top.wiki.storage;

import com.top.wiki.model.LogEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

/**
 * Wikipedia PageView operator class that implements the Storage Service interface
 */
@Slf4j
public class WikiPageViewFileOperator implements StorageService<Set<LogEntry>, Path> {

    /**
     * writes into the path from logentries
     * @param logEntries the logentries
     * @param out as output to write the logentries to
     * @return true if write were success false otherwise
     */
    @Override
    public boolean write(Set<LogEntry> logEntries, Path out) {
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            for (LogEntry le : logEntries) {
                bw.write(le.getDomainCode());
                bw.write("\t");
                bw.write(le.getPageTitle());
                bw.write("\t");
                bw.write(String.valueOf(le.getCountViews()));
                bw.write("\n");
            }
        } catch (IOException e) {
            log.info("writing in file: {}, {}", out, e.getMessage());
            return false;
        }
        return true;
    }

}
