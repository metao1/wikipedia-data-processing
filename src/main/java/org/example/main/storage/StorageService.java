package org.example.main.storage;

public interface StorageService<I, O> {
    boolean write(I in, O out);
}
