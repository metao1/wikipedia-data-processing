package com.top.wiki.connectivity;

import java.util.List;

/**
 * Interface for getting blacklist file remotely
 */
public interface BlackListConnectivityService {
    /**
     * Fetches the blacklist from remote server
     * @param blacklistUrl url of the expected blacklist that hosts on server
     * @return the list of string line by line content of the blacklist file
     */
    List<String> fetchBlacklist(String blacklistUrl);
}
