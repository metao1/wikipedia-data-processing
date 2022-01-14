package org.example.main.parser;

import org.example.main.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.example.main.util.Constants.DTF;

public abstract class TimeParser {

    TimeParser(String startStrDate, String startStrHour, String endStrDate, String endStrHour) throws DateTimeParseException {
        var startDate = LocalDateTime.parse(startStrDate + " " + startStrHour, DTF);
        var endDate = LocalDateTime.parse(endStrDate + " " + endStrHour, DTF);
        parseDates(startDate, endDate);
    }

    TimeParser() throws DateTimeParseException {
        var startStrDate = DateTimeUtil.getLocalDateString();
        var startHour = DateTimeUtil.getLocalTime();
        var endDateTime = LocalDateTime.parse(startStrDate + " " + startHour, DTF);
        var startDate = endDateTime.minusHours(1);
        parseDates(startDate, endDateTime);
    }

    protected abstract void parseDates(LocalDateTime startDate, LocalDateTime endDate);
}
