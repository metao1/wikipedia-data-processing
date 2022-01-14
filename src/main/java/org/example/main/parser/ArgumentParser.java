package org.example.main.parser;

import org.example.main.filter.StandardTimeIterator;

import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ArgumentParser {
    private final String[] args;

    public ArgumentParser(String[] args) {
        Objects.requireNonNull(args, "args can't be null");
        this.args = args;
    }

    public StandardTimeIterator parse() {
        int argsLen = this.args.length;
        StandardTimeIterator iterator;
        try {
            if (argsLen == 4) {
                iterator = new StandardTimeIterator(args[0], args[1], args[2], args[3]);
            } else if (argsLen == 0) {
                iterator = new StandardTimeIterator();
            } else {
                throw new RuntimeException("arguments number are wrong.");
            }
        } catch (DateTimeParseException parseException) {
            throw new RuntimeException(parseException.getMessage());
        }
        return iterator;
    }
}
