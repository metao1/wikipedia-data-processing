package com.top.wiki.util;

import com.top.wiki.model.LogEntry;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogEntryUtils {

    /**
     * Checks each line of record and parse it to LogEntry model
     *
     * @param line as input string format: domain_code page_title page_view_count
     * @return Parsed LogEntry model
     * @throws IndexOutOfBoundsException thrown if can't parse the input
     */
    public static LogEntry parseLineLogEntry(String line) throws IndexOutOfBoundsException {
        var s = line.split("\\s"); // split each item separated by space
        if (s.length < 3) { // malformed input
            throw new IndexOutOfBoundsException("the input must at least 3 spaces separated string");
        }
        return new LogEntry(s[0], s[1], Integer.parseInt(s[2]));
    }

    /**
     * Uses to parse blacklist entries which each record has zero page_view_count
     *
     * @param line as input string format: domain_code page_title
     * @return Parsed LogEntry model
     * @throws IndexOutOfBoundsException thrown if can't parse the input
     */
    public static LogEntry parseLineBlacklistLogEntry(String line) throws IndexOutOfBoundsException {
        String[] s = line.split("\\s");
        if (s.length < 2) { // malformed input
            throw new IndexOutOfBoundsException("the input must at least 2 spaces separated string");
        }
        return new LogEntry(s[0], s[1], 0);
    }

    /**
     * Creates input stream from input string
     * and then converts them to entries
     * and then converge them to list of logentries
     *
     * @param is input string as stream input
     * @return list of LinkedListed LogEntries with order of parsing
     */
    public static List<LogEntry> mapToLogEntry(Stream<String> is) {
        return is.flatMap(LogEntryUtils::parseToLogEntities)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static Tuple2<String, Path> urlToPathTuple(LocalDateTime time) {
        var fileName = StringUtils.buildStringWikiPageViewFilename(time);
        Path outputPath = Path.of(Constants.WIKIPEDIA_PAGE_VIEW_DIR, fileName);
        String url = StringUtils.buildStringWikiPageViewUrl(time);
        return Tuples.of(url, outputPath);
    }

    /**
     * Parses the input string to logentries stream in parallel
     *
     * @param st input stream
     * @return Stream of LogEntries
     */
    private static Stream<LogEntry> parseToLogEntities(String st) {
        try {
            return st.lines()
                    .parallel()
                    .map(line -> {
                        try {
                            return LogEntryUtils.parseLineLogEntry(line);
                        } catch (Exception ex) {
                            System.out.printf("warning: could not parse %s, reason: %s%n. ", line, ex.getMessage());
                        }
                        return null;
                    });
        } catch (Exception ex) {
            System.err.printf("error parsing %s: %s%n", st, ex.getMessage());
            return null;
        }
    }
}
