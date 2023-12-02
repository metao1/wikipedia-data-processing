package com.top.wiki.util;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Set of all constants in the application
 */
public class Constants {

    public static final DateTimeFormatter DTF_URL = DateTimeFormatter.ofPattern("yyyyMMdd-HH");
    public static final String BLACKLIST_URL = "https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages";
    public static final String WIKIPEDIA_BASE_PAGE_VIEW_URL = "https://dumps.wikimedia.org/other/pageviews";
    public static final String URL_REGEX = WIKIPEDIA_BASE_PAGE_VIEW_URL + "([\\/]?[\\-]?([\\w]+[\\/]?)(.gz)?)+";
    public static final String BLACKLIST_DIR = "blacklists";
    public static final String BLACKLIST_FILENAME = "blacklists.txt";
    public static final String WIKIPEDIA_PAGE_VIEW_DIR = "page_views";
    public static final String WIKI_PATH_REGEX = "([\\d]{8})-([\\d]{6})";
    public static final int MAX_IO_THREAD_COUNT = 3;
    public static final Duration TIMEOUT = Duration.ofSeconds(120);
}
