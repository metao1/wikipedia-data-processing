package org.example.main.util;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    public static final String BLACKLIST_URL = "https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages";
    public static final String WIKIPEDIA_BASE_PAGE_VIEW_URL = "https://dumps.wikimedia.org/other/pageviews";
    public static final String BLACKLIST_DIR = "blacklists";
    public static final String BLACKLIST_FILENAME = "blacklists.txt";
    public static final String WIKIPEDIA_FILE_PREFIX = "wiki.gz";
    public static final String WIKIPEDIA_PAGE_VIEW_DIR = "page_views";
    public static final String WIKI_PATH_REGEX = "([\\d]{8})-([\\d]{6})";
    public static final String URL_REGEX = WIKIPEDIA_BASE_PAGE_VIEW_URL + "([\\/]?[\\-]?([\\w]+[\\/]?)(.gz)?)+";
}
