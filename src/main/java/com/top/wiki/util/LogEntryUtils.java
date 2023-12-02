package com.top.wiki.util;

import com.top.wiki.model.LogEntry;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class LogEntryUtils {

    public static Tuple2<String, Path> urlToPathTuple(LocalDateTime time) {
        var fileName = WikiStringUtils.buildStringWikiPageViewFilename(time);
        Path outputPath = Path.of(Constants.WIKIPEDIA_PAGE_VIEW_DIR, fileName);
        String url = WikiStringUtils.buildWikiPageViewUrlFromTime(time);
        return Tuples.of(url, outputPath);
    }

    /**
     * Build a map from black list to help increase searching in blacklist with O(1)
     *
     * @param blackList the original blacklist items as string
     * @return set of mapped blacklist items by key as by page_title + " " + page_count_view
     */
    private Set<String> deduplicateEntries(List<String> blackList) {
        return blackList
                .parallelStream()
                .map(line -> {
                    try {
                        return LogEntry.buildLogEntry(line);
                    } catch (IndexOutOfBoundsException e) {
                        log.info("warning: cannot parse {} line of blacklist input", line);
                    }
                    return null;
                })
                .filter(le -> le != null && WikiStringUtils.notBlank(le.getDomainCodeAndPageTitle()))
                .map(LogEntry::getDomainCodeAndPageTitle)
                .collect(Collectors.toSet());
    }
}
