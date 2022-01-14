package org.example.main.storage;

public interface FileStorage<I, O> {
    String write(I in, O out);
}
