package com.top.wiki.connectivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface that turns any url to InputStream
 * for defining connection methods to deal with Network connection.
 */
public interface InputStreamConnectionService {

    /**
     * Gets an InputStream from url of remote host in Network.
     * @param url Address of the remote host
     * @return InputStream after establish connection to remote host.
     * @throws IOException if any connection problem occurs
     */
    InputStream get(String url) throws IOException;

}
