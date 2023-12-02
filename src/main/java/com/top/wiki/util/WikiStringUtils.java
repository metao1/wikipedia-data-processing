package com.top.wiki.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Set of utilities for string manipulation
 */
@UtilityClass
public class WikiStringUtils {

    /**
     * checks if the string is not blank or empty
     *
     * @param st the source input to check
     * @return true if the source input is not blank or empty
     */
    public static boolean notBlank(String st) {
        return (st != null && !st.isEmpty());
    }

    /**
     * creates filename from year-month-day hour format
     * filename would format in yearmonthday-hour.txt e.g. 20121202-12.txt
     *
     * @param time the input time as LocalDatetime to build from
     * @return the filename in string
     */
    public static String buildStringWikiPageViewFilename(LocalDateTime time) {
        var hour = DateTimeUtil.pad(time.getHour());
        var year = DateTimeUtil.pad(time.getYear());
        var month = DateTimeUtil.pad(time.getMonthValue());
        var day = DateTimeUtil.pad(time.getDayOfMonth());
        return String.format("%s-%s-%s-%s.txt", year, month, day, hour);
    }

    /**
     * creates url paths to wikipedia page view host from year-month-day hour format
     * e.g. "https://dumps.wikimedia.org/other/pageviews/2019/2019-02/projectviews-20190201-200000.gz"
     *
     * @param time input LocalDateTime
     * @return the formatted url in string
     */
    public static String buildWikiPageViewUrlFromTime(LocalDateTime time) {
        var hour = DateTimeUtil.pad(time.getHour());
        var year = DateTimeUtil.pad(time.getYear());
        var month = DateTimeUtil.pad(time.getMonthValue());
        var day = DateTimeUtil.pad(time.getDayOfMonth());
        return String.format("%s/%s/%s-%s/pageviews-%s%s%s-%s0000.gz", Constants.WIKIPEDIA_BASE_PAGE_VIEW_URL, year, year, month, year, month, day, hour);
    }

    /**
     * Build DateTime format from url of Page View Wikipedia
     *
     * @param url of given pageview wikipedia page
     * @return the LocalDateTime equivalent for that URL
     */
    public static LocalDateTime buildDateTimeFromUrl(String url) {
        String strDateTime = Pattern.compile(Constants.WIKI_PATH_REGEX)
                .matcher(url)
                .results()
                .map(MatchResult::group)
                .findFirst().orElse("");
        if (WikiStringUtils.notBlank(strDateTime)) {
            return LocalDateTime.parse(strDateTime.replaceAll("0000", ""), Constants.DTF_URL);
        } else {
            return null;
        }
    }

    /**
     * Given url extract the identification like yyyyMMdd-HH from url
     *
     * @param url given url
     * @return identification e.g. 20121212-01
     */
    public static String extractIdentification(String url) {
        Pattern pattern = Pattern.compile(Constants.WIKI_PATH_REGEX);
        Optional<String> item = pattern.matcher(url)
                .results()
                .map(MatchResult::group)
                .findFirst();
        return item.orElse("");
    }
}
