package org.example.main.connectivity;

import java.util.List;

public interface BlackListConnectivityService {
    List<String> fetchBlacklist(String blacklistUrl);
}
