package com.top.wiki;

import com.top.wiki.util.DateTimeUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeUtilTest {

    @Test
    void toUtcTest() {
        LocalDateTime nowUtc = DateTimeUtil.toUtc(LocalDateTime.now());
        LocalDate nowDateUtc = nowUtc.toLocalDate();
        OffsetDateTime expectedNowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        var startStrDate = expectedNowUtc.toLocalDate().toString();
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(startStrDate, dtf);
        assertEquals(localDate, nowDateUtc);
    }
}
