package com.top.wiki.connectivity;

import com.top.wiki.util.CollectionUtils;
import com.top.wiki.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.top.wiki.util.Constants.BLACKLIST_DIR;
import static com.top.wiki.util.Constants.BLACKLIST_FILENAME;

public class WikiPagesBlacklistConnectionService implements BlackListConnectivityService {

    private final InputStreamConnectionService httpConnection;

    public WikiPagesBlacklistConnectionService(InputStreamConnectionService httpConnection) {
        this.httpConnection = httpConnection;
    }

    @Override
    public List<String> fetchBlacklist(String blacklistUrl) {
        System.out.println("fetching blacklist from Url = " + blacklistUrl);
        Path blacklistPath = Path.of(BLACKLIST_DIR, BLACKLIST_FILENAME);
        Objects.requireNonNull(blacklistUrl, "blacklist url can't be null");
        boolean blackListFileMissed = blackListFileMissed(blacklistPath);
        if (blackListFileMissed) {
            createBlackListFile(blacklistPath);
            fetchRemoteBlackListAndSaveInDisk(blacklistUrl, blacklistPath);
        }
        List<String> lines = readBlacklistFile(blacklistPath);
        System.out.println("fetched blacklist successfully");
        return CollectionUtils.nonEmptyListConvertor(lines);
    }

    private void fetchRemoteBlackListAndSaveInDisk(String blacklistUrl, Path path) {
        try (InputStream inputStream = httpConnection.get(blacklistUrl)) {
            if (inputStream == null) {
                throw new RuntimeException("could not retrieve stream from blacklist service");
            }
            FileUtils.copyInputStreamToFile(inputStream, path);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private boolean blackListFileMissed(Path blacklistPath) {
        return FileUtils.fileNotExists(blacklistPath);
    }

    /**
     * Reads from the blacklist path
     * @param path to the blacklist file location on device
     * @return list of blacklist file content as string
     */
    private List<String> readBlacklistFile(Path path) {
        var fileAsStringList = FileUtils.convertFileToLines(path);
        return CollectionUtils.nonEmptyListConvertor(fileAsStringList);
    }

    /**
     * Creates a blacklist directory if not exist
     * @param blacklistPath path to store the blacklist file
     */
    private void createBlackListFile(Path blacklistPath) {
        if (FileUtils.fileNotExists(blacklistPath)) {
            FileUtils.createDirectory(blacklistPath);
        }
    }
}
