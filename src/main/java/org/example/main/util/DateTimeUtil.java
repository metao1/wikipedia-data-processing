package org.example.main.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static LocalDateTime toZone(final LocalDateTime time, final ZoneId fromZone, final ZoneId toZone) {
        final ZonedDateTime zonedTime = time.atZone(fromZone);
        final ZonedDateTime converted = zonedTime.withZoneSameInstant(toZone);
        return converted.toLocalDateTime();
    }

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

    public static String pad(int hour) {
        if (hour < 10) {
            return String.format("0%s", hour);
        }
        return String.valueOf(hour);
    }
}
