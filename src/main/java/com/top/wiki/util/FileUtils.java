package com.top.wiki.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for file operation and conversions
 */
public class FileUtils {

    /**
     * Copies the InputStream to destination file
     * @param input as source InputStream
     * @param path as the path to which the input will saved
     */
    public static void copyInputStreamToFile(InputStream input, Path path) {
        Objects.requireNonNull(path, "path can't be null");
        try (OutputStream output = new FileOutputStream(path.toFile())) {
            input.transferTo(output);
        } catch (IOException ioException) {
            System.err.println("error while save input to file:" + ioException.getMessage());
        }
    }

    /**
     * Checks if file does not exist
     * @param path the path address to check
     * @return true if file does not exists
     */
    public static boolean fileNotExists(Path path) {
        return Files.notExists(path);
    }

    /**
     * Reads the source path as a returns list of file contents as lines of strings
     * It parses the input as set of lines
     * @param path the source file to read from
     * @return the list of file contents as lines of strings
     */
    public static List<String> convertFileToLines(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Checks the path if not exists and create the directory from that path
     * @param path the path to check
     */
    public static void checkDirectory(Path path) {
        if (FileUtils.fileNotExists(path)) {
            try {
                Files.createDirectories(path.getParent());
                System.out.printf("directory %s created%n", path);
            } catch (IOException e) {
                System.err.println("error while creating directory:" + e.getMessage());
            }
        } else {
            System.out.println("skipping processing since file " + path.getFileName() + " already exists");
        }
    }

    /**
     * Creates a directory
     * @param path the path to create directory
     */
    public static void createDirectory(Path path) {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException("file already existed: " + e.getMessage());
        }
    }
}
