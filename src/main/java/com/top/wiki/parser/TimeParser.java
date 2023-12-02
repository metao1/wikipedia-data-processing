package com.top.wiki.parser;

import com.top.wiki.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class TimeParser {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    TimeParser(String startStrDate, String startStrHour, String endStrDate, String endStrHour) throws DateTimeParseException {
        var startDate = LocalDateTime.parse(startStrDate + " " + startStrHour, DTF);
        var endDate = LocalDateTime.parse(endStrDate + " " + endStrHour, DTF);
        parseDates(startDate, endDate);
    }

    /**
     * The default DateTimeParser that used when no dates where given.
     * It uses current time and converts it to utc format, if not exists the last hour.
     */
    TimeParser() {
        var startStrDate = DateTimeUtil.getLocalDateString();
        var startHour = DateTimeUtil.getLocalTime();
        var endDateTime = LocalDateTime.parse(startStrDate + " " + DateTimeUtil.pad(startHour), DTF);
        var startDate = endDateTime.minusHours(1);
        parseDates(startDate, endDateTime);
    }

    /**
     * Abstract concept for parsing the startDate and endDate in any way we want
     *
     * @param startDate start date
     * @param endDate   end date
     */
    protected abstract void parseDates(LocalDateTime startDate, LocalDateTime endDate);
}
