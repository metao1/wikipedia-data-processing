package com.top.wiki;

import com.top.wiki.model.LogEntry;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LogEntryTest {

    @Test
    public void givenLogEntries_whenSorting_usingareInOrder() {
        Stream<LogEntry> generatedLogEntriesDomainEs_s = IntStream.range(0, 10)
                .boxed()
                .map(i -> new LogEntry("en.s", "title" + i, i));

        Stream<LogEntry> generatedLogEntriesDomainAb_b = IntStream.range(0, 10)
                .boxed()
                .map(i -> new LogEntry("ab.b", "title" + i, i));

        Set<LogEntry> aggregatedSets =  Stream.concat(generatedLogEntriesDomainAb_b,generatedLogEntriesDomainEs_s)
                .collect(Collectors.toUnmodifiableSet());



        aggregatedSets
                .stream()
                .map(LogEntry::getDomainCode)
                .forEach(System.out::println);

    }
}
