package com.top.wiki;

import com.top.wiki.connectivity.BlacklistConnection;
import com.top.wiki.connectivity.HttpConnection;
import com.top.wiki.connectivity.StandardHttp;
import com.top.wiki.util.Constants;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BlackListServiceTest {

    HttpConnection httpConnection = Mockito.mock(StandardHttp.class);

    @Test
    @SneakyThrows
    void givenBlacklistUrl_getBlackList_isSuccess() {

        //when
        var is = getClass().getClassLoader().getResourceAsStream("blacklists.txt");
        when(httpConnection.read(Constants.BLACKLIST_URL)).thenReturn(is);

        //given
        BlacklistConnection blackListConnection = new BlacklistConnection(httpConnection);
        var blacklist = blackListConnection.fetchBlacklist(Constants.BLACKLIST_URL);

        //then
        verify(httpConnection).read(Constants.BLACKLIST_URL);

        assertThat(blacklist)
                .isNotNull()
                .hasSize(61);

    }
}