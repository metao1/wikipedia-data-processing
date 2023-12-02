package com.top.wiki.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for DateTime conversion operation.
 */
@UtilityClass
public class DateTimeUtil {

    /**
     * Converts the datetime to desire timezone
     * @param time input time
     * @param fromZone from the source timezone
     * @param toZone to destination timezone
     * @return the converted time timezone
     */
    public static LocalDateTime toZone(final LocalDateTime time, final ZoneId fromZone, final ZoneId toZone) {
        final ZonedDateTime zonedTime = time.atZone(fromZone);
        final ZonedDateTime converted = zonedTime.withZoneSameInstant(toZone);
        return converted.toLocalDateTime();
    }

    /**
     * Converts the datetime to UTC format
     * @param time input time
     * @param fromZone from the source timezone
     * @return the converted time to UTC timezone
     */
    public static LocalDateTime toUtc(final LocalDateTime time, final ZoneId fromZone) {
        return toZone(time, fromZone, ZoneOffset.UTC);
    }

    public static String getLocalDateString() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return now.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static int getLocalTime() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return now.getHour();
    }

    public static LocalDateTime toUtc(final LocalDateTime time) {
        return toUtc(time, ZoneId.systemDefault());
    }

    /**
     * Convert the string equivalent of the hour as input
     * e.g. 1-> 01, 10-> 10
     * @param hour as input
     * @return the String equivalent hours as padded hour
     */
    public static String pad(int hour) {
        if (hour < 10) {
            return String.format("0%s", hour);
        }
        return String.valueOf(hour);
    }
}
