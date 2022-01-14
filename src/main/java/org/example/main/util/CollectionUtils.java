package org.example.main.util;

import org.example.main.model.LogEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtils {

    public static <T> List<T> nonEmptyListConvertor(List<T> inputList) {
        boolean isNullOrEmpty = (inputList == null || inputList.isEmpty());
        return isNullOrEmpty ? List.of() : inputList;
    }

    public static Set<LogEntry> convertToSet(List<LogEntry> filteredLogEntries) {
        return new HashSet<>(filteredLogEntries);
    }

}
