package com.top.wiki.model;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.nullsLast;
import static java.util.Comparator.reverseOrder;

/**
 * Model describes of each line of Page view file
 * format is domain page_title count_views
 */
@Value
@Slf4j
public class LogEntry implements Comparable<LogEntry> {

    String domainCode;
    String pageTitle;
    // combined domain + " " + page_title
    String domainCodeAndPageTitle;
    Integer countViews;

    public LogEntry(String domainCode, String pageTitle, Integer countViews) {
        this.domainCode = domainCode;
        this.pageTitle = pageTitle;
        this.countViews = countViews;
        this.domainCodeAndPageTitle = domainCode + " " + pageTitle;
    }

    /**
     * Helper function to sort PageEntry based-on domain name first and then (domain + " " + page_title)
     */
    private static final Comparator<LogEntry> COMPARATOR = Comparator
            .comparing(LogEntry::getDomainCode)
            .thenComparing(LogEntry::getDomainCodeAndPageTitle, nullsLast(reverseOrder()))
            .thenComparing(LogEntry::getCountViews, nullsLast(reverseOrder()));

    @Override
    public int compareTo(@NotNull LogEntry other) {
        return COMPARATOR.compare(this, other);
    }

    /**
     * Uses to parse blacklist entries which each record has zero page_view_count
     *
     * @param line as input string format: domain_code page_title
     * @return Parsed LogEntry model
     * @throws IndexOutOfBoundsException thrown if can't parse the input
     */
    public static LogEntry buildLogEntry(String line) throws IndexOutOfBoundsException {
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
        return is.parallel()
                .map(line -> {
                    try {
                        return parseLineLogEntry(line);
                    } catch (Exception ex) {
                        log.warn("could not parse {}, reason: {}. ", line, ex.getMessage());
                    }
                    return null;
                }).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Checks each line of record and parse it to LogEntry model
     *
     * @param line as input string format: domain_code page_title page_view_count
     * @return Parsed LogEntry model
     * @throws IndexOutOfBoundsException thrown if can't parse the input
     */
    private static LogEntry parseLineLogEntry(String line) throws IndexOutOfBoundsException {
        var s = line.split("\\s+"); // split each item separated by space
        if (s.length < 3) { // malformed input
            throw new IndexOutOfBoundsException("the input must at least 3 spaces separated string");
        }
        return new LogEntry(s[0], s[1], Integer.parseInt(s[2]));
    }
}
