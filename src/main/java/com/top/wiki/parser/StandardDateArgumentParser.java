package com.top.wiki.parser;

import com.top.wiki.filter.StandardTimeIterator;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Objects;

/**
 * StandardDateArgumentParser is a Builder class as name suggests parses input arguments.
 * It does this by calling parse() method and returning Iterator<LocalDateTime>
 */
public class StandardDateArgumentParser {
    private final String[] args;

    /**
     * Standard constructor for parsing array of String objects as input.
     * Inputs will parse all in LocalDateTime format.
     * Actual inputs are in in current user local datetime, and will be parsed to UTC time.
     *
     * @param args as input string array
     */
    public StandardDateArgumentParser(String[] args) {
        Objects.requireNonNull(args, "args can't be null");
        this.args = args;
    }

    /**
     * Paring the arguments as String array initiated in constructor.
     * Iterators can be StandardTimeIterator or any other type that implement
     * Iterator Interface.
     * Parse method decide if no input were given, then current datetime will be used
     * as default StandardTimeIterator, otherwise passes given arguments.
     *
     * @return instance of any type of Iterator<LocalDateTime>
     */
    public Iterator<LocalDateTime> parse() {
        StandardTimeIterator iterator;
        try {
            if (args.length == 4) {
                // Arguments are in string in order "start date","start time","end date","end time"
                iterator = new StandardTimeIterator(args[0], args[1], args[2], args[3]);
            } else if (args.length == 0) {
                // Current datetime will be used
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
