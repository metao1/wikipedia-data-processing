package com.top.wiki.connectivity;

import java.io.IOException;

/**
 * An interface that checks the existence of the url
 */
public interface UrlCheckoutService {

    /**
     * Gets an InputStream from url of remote host in Network.
     * @param url Address of the remote host
     * @return InputStream after establish connection to remote host.
     * @throws IOException if any connection problem occurs
     */
    boolean exists(String url) throws IOException;

}
