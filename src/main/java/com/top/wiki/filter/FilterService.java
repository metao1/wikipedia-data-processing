package com.top.wiki.filter;

import java.util.List;

/**
 * Filter Service abstract interface for applying filter over entries
 *
 * @param <T> the type of entries
 */
public interface FilterService<T> {

    /**
     * Gets blacklist items from remote server and applies them to entries
     *
     * @param blackListUrl remote http url address of blacklist filter server
     * @param entries      as entries to applying the filter on
     */
    void applyFilteredEntries(String blackListUrl, final List<T> entries);
}
