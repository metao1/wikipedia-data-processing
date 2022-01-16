package com.top.wiki.filter;

import java.time.LocalDateTime;

/**
 * Time Interface
 */
public interface TimeInterface {

    /**
     * Gets end time in LocalDateTime
     * @return end time
     */
    LocalDateTime getEndTime();

    /**
     * Gets current time in LocalDateTime
     * @return current time
     */
    LocalDateTime getCurrentTime();

    /**
     * updates current time, points to the next desire time, and saves it for next move
     * @param time input time to update
     */
    void updateCurrentTimeUtc(LocalDateTime time);

}
