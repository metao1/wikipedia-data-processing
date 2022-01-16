package com.top.wiki.storage;

/**
 * Simple interface for storage services uses Input/Output as type.
 * @param <I> Input type
 * @param <O> Output type
 */
public interface StorageService<I, O> {
    /**
     * Write the input to output
     * @param in input
     * @param out output
     * @return true if the write was successful, false otherwise
     */
    boolean write(I in, O out);
}
