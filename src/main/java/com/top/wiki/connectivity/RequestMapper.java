package com.top.wiki.connectivity;

import okhttp3.Request;

public interface RequestMapper {


    Request createRequest(String url, String encoding);
}
