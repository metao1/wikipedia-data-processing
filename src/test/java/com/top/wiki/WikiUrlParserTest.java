package com.top.wiki;

import com.top.wiki.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WikiUrlParserTest {

    @Test
    public void givenUrl_thenParsingToFileName_isSuccessful() {
        var wikiPageViewStringUrl = "https://dumps.wikimedia.org/other/pageviews/1231/asda/pageviews-20201212-100000.gz";
        Pattern pattern = Pattern.compile(Constants.WIKI_PATH_REGEX);
        Optional<String> item = pattern.matcher(wikiPageViewStringUrl)
                .results()
                .map(MatchResult::group)
                .findFirst();
        String num = item.orElse("");
        assertEquals("20201212-100000", num);
    }

    @Test
    public void givenUrl_thenParsingWikiUrl_isSuccessful() {
        var wikiPageViewStringUrl = "https://dumps.wikimedia.org/other/pageviews/2022/2022-01/pageviews-20220114-230000.gz";
        boolean matches = wikiPageViewStringUrl.matches(Constants.URL_REGEX);
        assertTrue(matches);
    }

}
