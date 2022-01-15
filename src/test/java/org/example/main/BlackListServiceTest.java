package org.example.main;

import org.example.main.connectivity.BlackListConnectivityService;
import org.example.main.connectivity.StandardInputStreamService;
import org.example.main.connectivity.WikiPagesBlacklistConnectionService;
import org.example.main.util.Constants;
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