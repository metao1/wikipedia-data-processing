package com.top.wiki.util;

import com.top.wiki.connectivity.CheckConnectionService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility class for converting Date and Times to different data structures
 */
public class TimeUtilsService {
    private final CheckConnectionService checkConnectionService;

    public TimeUtilsService(CheckConnectionService checkConnectionService) {
        this.checkConnectionService = checkConnectionService;
    }

    /**
     * Converts a list of LocalDateTime from Iterator in parallel which are in order, immutable and nonnull
     *
     * @param timeItr the time iterator
     * @return List of LocalDateTime that is in order of timeIterator
     */
    public List<LocalDateTime> getTimeList(Iterator<LocalDateTime> timeItr) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(timeItr, Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL), true)
                .map(this::checkDateTimeExists)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public LocalDateTime checkDateTimeExists(LocalDateTime time) {
        try {
            int maxTry = 24;
            while (maxTry > 0 && !checkConnectionService.exists(StringUtils.buildStringWikiPageViewUrl(time))) {
                System.err.printf("The remote %s does not exits", time);
                TimeUnit.MILLISECONDS.sleep(200);
                time = time.minusHours(1);
                System.err.printf(" retrying  %s.%n", time);
                maxTry--;
            }
            return time;
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return time;
    }
}
