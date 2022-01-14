package org.example.main.filter;

import org.example.main.parser.StandardTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StandardTimeIterator implements Iterator<LocalDateTime> {

    private final TimeInterface timeInterface;

    public StandardTimeIterator(String var1, String var2, String var3, String var4) throws DateTimeParseException {
        timeInterface = new StandardTimeParser(var1, var2, var3, var4);
    }

    public StandardTimeIterator() throws DateTimeParseException {
        timeInterface = new StandardTimeParser();
    }

    @Override
    public boolean hasNext() {
        return timeInterface.getCurrentTime().isBefore(timeInterface.getEndTime()) && !timeInterface.getCurrentTime().isEqual(timeInterface.getEndTime());
    }

    @Override
    public LocalDateTime next() {
        if (!hasNext()) {
            throw new NoSuchElementException("end of times reached");
        }
        var ret = timeInterface.getCurrentTime();
        timeInterface.updateCurrentTimeUtc(ret.plusHours(1));
        return ret;
    }
}
