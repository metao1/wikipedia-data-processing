package org.example.main.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class FileUtils {

    public static void copyInputStreamToFile(InputStream input, Path path) {
        Objects.requireNonNull(path, "path can't be null");
        try (OutputStream output = new FileOutputStream(path.toFile())) {
            input.transferTo(output);
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    public static boolean fileNotExists(Path path) {
        return Files.notExists(path);
    }

    public static List<String> convertFileToLines(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException("file already existed: " + e.getMessage());
        }
    }
}
