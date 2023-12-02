package com.top.wiki.connectivity;

import com.top.wiki.util.WikiStringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class TimeFilterService {

    private final UrlCheckoutService urlCheckoutService;

    public void checkRemoteUrlExistence(LocalDateTime time, Consumer<LocalDateTime> consumer) {
        var url = WikiStringUtils.buildWikiPageViewUrlFromTime(time);
        try {
            if (urlCheckoutService.exists(url)) {
                consumer.accept(time);
            }
        } catch (IOException e) {
            log.warn("the specified URL {} not resolved", url);
        }
    }
}
