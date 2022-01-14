package org.example.main.parser;

import org.example.main.filter.TimeInterface;
import org.example.main.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

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
