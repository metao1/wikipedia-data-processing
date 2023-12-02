package com.top.wiki;

import com.top.wiki.parser.TimeParser;
import com.top.wiki.util.WikiStringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WikiStringUtilsTest {

    @Test
    public void givenUrl_returnLocalDatetime_isSuccess() {
        var url = "https://dumps.wikimedia.org/other/pageviews/2022/2022-01/pageviews-20220116-210000.gz";
        LocalDateTime parsedDateTime = WikiStringUtils.buildDateTimeFromUrl(url);
        assertNotNull(parsedDateTime);
        assertEquals(parsedDateTime, LocalDateTime.parse("2022-01-16 21", TimeParser.DTF));
    }
}
