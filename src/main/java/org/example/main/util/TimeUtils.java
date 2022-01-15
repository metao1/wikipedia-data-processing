package org.example.main.util;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility class for converting Date and Times to different data structures
 */
public class TimeUtils {

    /**
     * Converts a list of LocalDateTime from Iterator in parallel which are in order, immutable and nonnull
     *
     * @param timeItr the time iterator
     * @return List of LocalDateTime that is in order of timeIterator
     */
    public static List<LocalDateTime> getTimeList(Iterator<LocalDateTime> timeItr) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(timeItr, Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL), true)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
