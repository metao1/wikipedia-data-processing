package org.example.main.stream;

import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class Memoization {

    public static <T> Supplier<Stream<T>> replay(Supplier<Stream<T>> dataSrc) {
        final Recorder<T> rec = new Recorder<T>(dataSrc);
        return () -> {
            Spliterator<T> iter = rec.memIterator();
            return stream(iter, false);
        };
    }
}
