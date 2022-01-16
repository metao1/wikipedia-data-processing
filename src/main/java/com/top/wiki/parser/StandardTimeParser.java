package com.top.wiki.parser;

import com.top.wiki.filter.TimeInterface;
import com.top.wiki.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Standard Time Parser that parses String times and convert them in utc format.
 * Has current time utc that points to the head of list, start time and end time.
 */
public class StandardTimeParser extends TimeParser implements TimeInterface {

    private LocalDateTime currentTimeUtc;
    private LocalDateTime startTimeUtc;
    private LocalDateTime endTimeUtc;

    public StandardTimeParser(String startStrDate, String startStrHour, String endStrDate, String endStrHour) throws DateTimeParseException {
        super(startStrDate, startStrHour, endStrDate, endStrHour);
    }

    public StandardTimeParser() throws DateTimeParseException {
        super();
    }

    @Override
    protected void parseDates(LocalDateTime startDate, LocalDateTime endDate) {
        // toUtc converts input from Local Date Time to utc
        this.startTimeUtc = DateTimeUtil.toUtc(startDate);
        this.currentTimeUtc = startTimeUtc;
        this.endTimeUtc = DateTimeUtil.toUtc(endDate);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTimeUtc;
    }

    @Override
    public LocalDateTime getCurrentTime() {
        return currentTimeUtc;
    }

    @Override
    public void updateCurrentTimeUtc(LocalDateTime time) {
        this.currentTimeUtc = time;
    }
}
