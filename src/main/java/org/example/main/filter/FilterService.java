package org.example.main.filter;

import java.util.List;

public interface FilterService<T> {

    List<T> applyAndGetFilteredEntries(String blackListUrl, final List<T> entries);
}
