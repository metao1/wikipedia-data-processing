package org.example.main.util;

import org.example.main.model.LogEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogEntryUtils {

    public static LogEntry parseLineLogEntry(String line) throws IndexOutOfBoundsException {
        var s1 = line.indexOf(' ');
        var s2 = line.indexOf(' ', s1 + 1);
        var s3 = line.indexOf(' ', s2 + 1);
        return new LogEntry(line.substring(0, s1), line.substring(s1 + 1, s2), Integer.parseInt(line.substring(s2 + 1, s3)));
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
                            System.out.printf("warning: parsing line %s error, reason %s%n. ", line, ex.getMessage());
                        }
                        return null;
                    });
        } catch (Exception ex) {
            System.err.printf("error parsing %s: %s%n", st, ex.getMessage());
            return null;
        }
    }
}
