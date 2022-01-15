package org.example.main.util;

import org.example.main.model.LogEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogEntryUtils {

    public static LogEntry parseLineLogEntry(String line) throws IndexOutOfBoundsException {
        var s = line.split("\\s");
        if (s.length < 3) {
            return null;
        }
        return new LogEntry(s[0], s[1], Integer.parseInt(s[2]));
    }

    public static LogEntry parseLineBlacklistLogEntry(String line) throws IndexOutOfBoundsException {
        String[] lines = line.split("\\s");
        return new LogEntry(lines[0], lines[1], 0);
    }

    public static List<LogEntry> mapToLogEntry(Stream<String> is) {
        return is.flatMap(LogEntryUtils::parseToLogEntities)
                .collect(Collectors.toCollection(LinkedList::new));
    }

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
