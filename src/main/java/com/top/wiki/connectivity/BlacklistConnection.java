package com.top.wiki.connectivity;

import com.top.wiki.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.top.wiki.util.Constants.BLACKLIST_DIR;
import static com.top.wiki.util.Constants.BLACKLIST_FILENAME;

@Slf4j
@RequiredArgsConstructor
public class BlacklistConnection {

    private final HttpConnection httpConnection;

    public List<String> fetchBlacklist(String blacklistUrl) {
        Objects.requireNonNull(blacklistUrl, "blacklist url can't be null");
        log.info("fetching blacklist from Url = " + blacklistUrl);
        Path blacklistPath = Path.of(BLACKLIST_DIR, BLACKLIST_FILENAME);
        boolean blackListFileMissed = blackListFileMissed(blacklistPath);
        if (!blackListFileMissed) {
            createBlackListFile(blacklistPath);
            try (var inputStream = fetchRemoteBlackListStream(blacklistUrl)) {
                FileUtils.copyInputStreamToFile(inputStream, blacklistPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<String> lines = readBlacklistFile(blacklistPath);
        log.info("fetched blacklist successfully");
        return lines;
    }

    private InputStream fetchRemoteBlackListStream(String blacklistUrl) throws IOException {
        InputStream inputStream = httpConnection.read(blacklistUrl);
        if (inputStream == null) {
            throw new RuntimeException("could not retrieve stream from blacklist service");
        }
        return inputStream;
    }

    private boolean blackListFileMissed(Path blacklistPath) {
        return FileUtils.fileNotExists(blacklistPath);
    }

    /**
     * Reads from the blacklist path
     *
     * @param path to the blacklist file location on device
     * @return list of blacklist file content as string
     */
    private List<String> readBlacklistFile(Path path) {
        return FileUtils.convertFileToLines(path);
    }

    /**
     * Creates a blacklist directory if not exist
     *
     * @param blacklistPath path to store the blacklist file
     */
    private void createBlackListFile(Path blacklistPath) {
        if (FileUtils.fileNotExists(blacklistPath)) {
            FileUtils.createDirectory(blacklistPath);
        }
    }
}
