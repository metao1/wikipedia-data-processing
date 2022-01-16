package com.top.wiki.model;

import java.util.Comparator;
import java.util.Objects;

import static java.util.Comparator.nullsLast;
import static java.util.Comparator.reverseOrder;

/**
 * Model describes of each line of Page view file
 * format is domain page_title count_views
 */
public class LogEntry implements Comparable<LogEntry> {

    private final String domainCode;
    private final String pageTitle;
    // combined domain + " " + page_title
    private final String domainCodeAndPageTitle;
    private final Integer countViews;
    protected final int HASH_MULTIPLIER = 31;
    protected Integer hash;

    public LogEntry(String domainCode, String pageTitle, Integer countViews) {
        this.domainCode = domainCode;
        this.pageTitle = pageTitle;
        this.countViews = countViews;
        this.domainCodeAndPageTitle = domainCode + " " + pageTitle;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public Integer getCountViews() {
        return countViews;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getDomainCodeAndPageTitle() {
        return domainCodeAndPageTitle;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return false;
        if (!(other instanceof LogEntry logEntry)) return false;
        return Objects.equals(domainCode, logEntry.domainCode)
                && Objects.equals(pageTitle, logEntry.pageTitle)
                && Objects.equals(domainCodeAndPageTitle, logEntry.domainCodeAndPageTitle)
                && Objects.equals(countViews, logEntry.countViews);
    }

    @Override
    public int hashCode() {
        // cache it
        if (this.hash == null) {
            this.hash = super.hashCode() * HASH_MULTIPLIER;
            this.hash += Objects.hash(this.countViews);
            this.hash += Objects.hash(this.pageTitle);
            this.hash += Objects.hash(this.domainCode);
            this.hash += Objects.hash(this.domainCodeAndPageTitle);
        }

        return this.hash;
    }

    /**
     * Helper function to sort PageEntry based-on domain name first and then (domain + " " + page_title)
     */
    private static final Comparator<LogEntry> COMPARATOR = Comparator
            .comparing(LogEntry::getDomainCode)
            .thenComparing(LogEntry::getDomainCodeAndPageTitle, nullsLast(reverseOrder()))
            .thenComparing(LogEntry::getCountViews, nullsLast(reverseOrder()));

    @Override
    public int compareTo(LogEntry other) {
        if (other == null) {
            return -1;
        }
        return COMPARATOR.compare(this, other);
    }

}
