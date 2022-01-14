package org.example.main.filter;

import java.time.LocalDateTime;

public interface TimeInterface {

    LocalDateTime getEndTime();

    LocalDateTime getCurrentTime();

    void updateCurrentTimeUtc(LocalDateTime time);

}
