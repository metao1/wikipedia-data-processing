package com.top.wiki.util;

import com.top.wiki.parser.StandardDateArgumentParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class InputParserServiceTest {

    @Test
    void getTimesBetweenStartEnd() {
        String[] args = new String[]{"2023-04-12", "14", "2023-04-13", "14"};
        var argParser = new StandardDateArgumentParser(args);

        Iterator<LocalDateTime> timeIterator = argParser.parse();

        TimeParserService timeService = new TimeParserService(timeIterator);
        List<LocalDateTime> timesBetweenStartEnd = timeService.getTimesBetweenStartEnd();

        assertThat(timesBetweenStartEnd)
                .isNotEmpty()
                .hasSize(24);

        args = new String[]{"2023-04-12", "14", "2023-05-13", "14"};
        argParser = new StandardDateArgumentParser(args);

        timeIterator = argParser.parse();

        timeService = new TimeParserService(timeIterator);
        timesBetweenStartEnd = timeService.getTimesBetweenStartEnd();

        assertThat(timesBetweenStartEnd)
                .isNotEmpty()
                .hasSize(24 * 31);

    }

}