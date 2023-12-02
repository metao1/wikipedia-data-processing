package com.top.wiki.parser;

import com.top.wiki.util.TimeParserService;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class StandardDateArgumentParserTest {

    @Test
    void givenStringArguments_parse_whenNoArgs_thenSuccess() {
        var argParser = new StandardDateArgumentParser(new String[]{});
        var time = argParser.parse();
        assertTrue(time.hasNext());
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        var startTime = time.next();
        assertEquals(startTime.getHour(), now.minusHours(2).getHour());
    }

    @Test
    void givenStringArguments_parse_whenManyArgs_whenWrongFormat_thenFailure() {
        var argParser = new StandardDateArgumentParser(new String[]{"2018-1211", "16", "2018-12-12", "17"});
        assertThrowsExactly(RuntimeException.class, argParser::parse);
    }

    @Test
    void givenStringArguments_parseMultipleHours_whenMultipleArgs_thenSuccess() {
        var argParser = new StandardDateArgumentParser(new String[]{"2018-12-11", "16", "2018-12-12", "17"});
        var timeItr = argParser.parse();
        assertNotNull(timeItr);
        var timeService = new TimeParserService(timeItr);
        var timeList = timeService.getTimesBetweenStartEnd();
        assertEquals(25, timeList.size());
    }

    @Test
    void givenStringArguments_parse_whenMultipleArgs_thenSuccess() {
        var argParser = new StandardDateArgumentParser(new String[]{"2018-12-11", "16", "2018-12-12", "17"});
        var time = argParser.parse();
        assertTrue(time.hasNext());
    }

    @Test
    void givenStringArguments_parse_whenWrongNumberArgs_thenFailure() {
        assertThrowsExactly(RuntimeException.class, () -> {
            var argParser = new StandardDateArgumentParser(new String[]{"2018-12-11", "2018-12-12", "17"});
            var time = argParser.parse();
            assertTrue(time.hasNext());
        }, "arguments number are wrong.");
    }


}