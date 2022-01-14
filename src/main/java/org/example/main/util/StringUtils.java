package org.example.main.util;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.example.main.util.Constants.WIKIPEDIA_BASE_PAGE_VIEW_URL;

public class StringUtils {

    public static boolean notBlank(String st) {
        return (st != null && !st.isEmpty());
    }

    public static String buildStringWikiPageViewFilename(String year, String month, String day, String hour) {
        return String.format("%s%s%s-%s.txt", year, month, day, hour);
    }

    public static String buildStringWikiPageViewUrl(String year, String month, String day, String hour) {
        return String.format("%s/%s/%s-%s/pageviews-%s%s%s-%s0000.gz", WIKIPEDIA_BASE_PAGE_VIEW_URL, year, year, month, year, month, day, hour);
    }

    public static String extractIdentification(String url) {
        Pattern pattern = Pattern.compile(Constants.WIKI_PATH_REGEX);
        Optional<String> item = pattern.matcher(url)
                .results()
                .map(MatchResult::group)
                .findFirst();
        return item.orElse("");
    }
}
