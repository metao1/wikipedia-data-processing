package org.example.main.util;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class StandardTimeUtils {

    public static List<LocalDateTime> getTimeList(Iterator<LocalDateTime> timeItr) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(timeItr, 0), true)
                .toList();
    }
}
