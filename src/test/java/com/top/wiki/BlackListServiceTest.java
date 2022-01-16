package com.top.wiki;

import com.top.wiki.connectivity.StandardInputStreamService;
import com.top.wiki.connectivity.WikiPagesBlacklistConnectionService;
import com.top.wiki.util.Constants;
import com.top.wiki.connectivity.BlackListConnectivityService;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackListServiceTest {

    @Test
    void givenBlacklistUrl_getBlackList_isSuccess() throws IOException {
        BlackListConnectivityService blackListService = new WikiPagesBlacklistConnectionService(new StandardInputStreamService());
        var blacklistSet = blackListService.fetchBlacklist(Constants.BLACKLIST_URL);
        assertNotNull(blacklistSet);
        assertTrue(blacklistSet.size() > 0);
    }
}