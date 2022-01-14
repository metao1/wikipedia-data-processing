package org.example.main.storage;

import org.example.main.model.LogEntry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

public class WikiPageViewFileOperator implements FileStorage<Set<LogEntry>, Path> {

    //    public String write(Flux<LogEntry> stringFlux, Path out) {
//        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
//            stringFlux
//                    .subscribe(s -> write(bw, s),
//                            (e) -> close(bw),  // close file if error / oncomplete
//                            () -> close(bw)
//                    );
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//        return out.toString();
//    }
//
//    private static void close(Closeable closeable) {
//        try {
//            closeable.close();
//            System.out.println("Closed the resource");
//            System.exit(0);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//
//    private static void write(BufferedWriter bw, LogEntry le) {
//        try {
//            bw.write(le.getDomainCode());
//            bw.newLine();
//            bw.write(le.getPageTitle());
//            bw.write("\t");
//            bw.write(String.valueOf(le.getCountViews()));
//            bw.newLine();
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//    }
//
    @Override
    public String write(Set<LogEntry> logEntries, Path out) {
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            for (LogEntry le : logEntries) {
                bw.write(le.getDomainCode());
                bw.write("\t");
                bw.write(le.getPageTitle());
                bw.write("\t");
                bw.write(String.valueOf(le.getCountViews()));
                bw.write("\n");
            }
        } catch (IOException e) {
            System.err.printf("error while writing in file: %s, %s%n", out, e.getMessage());
        }
        return out.getFileName().toString();
    }

}
