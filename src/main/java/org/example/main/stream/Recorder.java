package org.example.main.stream;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Recorder<T> {

    private final Spliterator<T> srcIter;
    private final long estimateSize;
    private boolean hasNext = true;
    private List<T> mem;

    public Recorder(Supplier<Stream<T>> dataSrc) {
        srcIter = dataSrc.get().spliterator();
        estimateSize = srcIter.estimateSize();
        mem = new LinkedList<>();
    }

    public Spliterator<T> memIterator() {
        return new MemoizeIter();
    }

    public synchronized boolean getOrAdvance(int index, Consumer<? super T> consumer) {
        if (index < mem.size()) {
            consumer.accept(mem.get(index));
            return true;
        } else if (hasNext) {
            hasNext = srcIter.tryAdvance(item -> {
                mem.add(item);
                consumer.accept(item);
            });
        }
        return hasNext;
    }


    class MemoizeIter extends Spliterators.AbstractSpliterator<T> {

        int index = 0;

        /**
         * Creates a spliterator reporting the given estimated size and
         * additionalCharacteristics.
         */
        protected MemoizeIter() {
            super(estimateSize, srcIter.characteristics());
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> consumer) {
            return getOrAdvance(index++, consumer);
        }

        public Comparator<? super T> getComparator() {
            return srcIter.getComparator();
        }
    }

}
