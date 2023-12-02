package com.top.wiki.filter;

import com.top.wiki.parser.StandardTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * StandardTimeIterator uses Iterate Pattern Design creates iteration over hours,
 * in the period of start to end datetime, for navigation over spectrum of LocalDateTimes.
 * It simply implements the Iterator interface that allow foreach() and also converting to different
 * Data Collectors like List or Set freely.
 * The class uses TimeInterface to connect to other Parsers and fetches data collection repository using this interface.
 */
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
        // checks if head reaches to the end of end time we set
        return !timeInterface.getCurrentTime().minusHours(1).isEqual(timeInterface.getEndTime());
    }

    /**
     * gets current time and move head to the next hour in list.
     * increases current time by 1 hour each time
     * @return current time
     */
    @Override
    public LocalDateTime next() {
        if (!hasNext()) {
            throw new NoSuchElementException("end of times reached");
        }
        var ret = timeInterface.getCurrentTime();
        //increases the current time hour by 1
        timeInterface.updateCurrentTimeUtc(ret.plusHours(1));
        return ret;
    }
}
