package com.top.wiki.util;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * Utility class for converting Date and Times to different data structures
 */
@RequiredArgsConstructor
public class TimeParserService {

    private final Iterator<LocalDateTime> timeItr;

    /**
     * Converts a list of LocalDateTime from Iterator to list of LocalDateTime in parallel which are in order, immutable and nonnull
     *
     * @return List of LocalDateTime that is in order of timeIterator
     */
    public List<LocalDateTime> getTimesBetweenStartEnd() {
        List<LocalDateTime> timeQueue = new LinkedList<>();
        for (LocalDateTime time = timeItr.next(); timeItr.hasNext() && time.isBefore(timeItr.next()); time = time.plusHours(1)) {
            timeQueue.add(time);
        }
        return timeQueue;
    }

    public List<LocalDateTime> getVerifiedTimesInParallel(BiConsumer<LocalDateTime, Consumer<LocalDateTime>> biConsumer) {
        var threadSafeFlags = Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL;
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(timeItr, threadSafeFlags), true)
                .mapMulti(biConsumer)
                .toList();
    }
}
