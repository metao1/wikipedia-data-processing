package org.example.main.connectivity;

import org.example.main.util.CollectionUtils;
import org.example.main.util.FileUtils;
import org.example.main.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.example.main.util.Constants.BLACKLIST_DIR;
import static org.example.main.util.Constants.BLACKLIST_FILENAME;

public class WikiPagesBlacklistConnectionService implements BlackListConnectivityService {

    private final ConnectionService httpConnection;

    public WikiPagesBlacklistConnectionService(ConnectionService httpConnection) {
        this.httpConnection = httpConnection;
    }

    @Override
    public List<String> fetchBlacklist(String blacklistUrl) {
        Path blacklistPath = Path.of(BLACKLIST_DIR, BLACKLIST_FILENAME);
        Objects.requireNonNull(blacklistUrl, "blacklist url can't be null");
        boolean blackListFileMissed = blackListFileMissed(blacklistPath);
        if (blackListFileMissed) {
            createBlackListFile(blacklistPath);
            InputStream is = fetchRemoteBlackList(blacklistUrl);
            StreamUtils.saveInputStreamToFile(is, blacklistPath);
        }
        List<String> lines = readBlacklistFile(blacklistPath);
        return CollectionUtils.nonEmptyListConvertor(lines);
    }

    private InputStream fetchRemoteBlackList(String blacklistUrl) {
        try (InputStream inputStream = httpConnection.get(blacklistUrl)) {
            if (inputStream == null) {
                throw new RuntimeException("could not retrieve stream from blacklist service");
            }
            return inputStream;
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private boolean blackListFileMissed(Path blacklistPath) {
        return FileUtils.fileNotExists(blacklistPath);
    }

    private List<String> readBlacklistFile(Path path) {
        var fileAsStringList = FileUtils.convertFileToLines(path);
        return CollectionUtils.nonEmptyListConvertor(fileAsStringList);
    }

    private void createBlackListFile(Path blacklistPath) {
        if (FileUtils.fileNotExists(blacklistPath)) {
            FileUtils.createDirectory(blacklistPath);
        }
    }
}
