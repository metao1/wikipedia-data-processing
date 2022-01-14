package org.example.main.util;

import org.example.main.model.LogEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LogEntryUtils {

    public static LogEntry parseLineLogEntry(String line) throws IndexOutOfBoundsException {
        String[] lines = line.split("\\s");
        if (line.length() < 3) {
            return null;
        }
        return new LogEntry(lines[0], lines[1], Integer.parseInt(lines[2]));
    }

    public static LogEntry parseLineBlacklistLogEntry(String line) throws IndexOutOfBoundsException {
        String[] lines = line.split("\\s");
        return new LogEntry(lines[0], lines[1], 0);
    }

    public static List<LogEntry> mapToLogEntry(String is) {
        return is.lines()
                .parallel()
                .map(LogEntryUtils::parseToLogEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static LogEntry parseToLogEntity(String st) {
        try {
            return parseLineLogEntry(st);
        } catch (Exception ex) {
            System.err.printf("error parsing %s: %s%n", st, ex.getMessage());
            return null;
        }
    }
}
