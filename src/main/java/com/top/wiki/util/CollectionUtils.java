package com.top.wiki.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Set of utilities for collection classes
 */
@UtilityClass
public class CollectionUtils {

    /**
     * return non-empty list of the equivalent list
     *
     * @param inputList original list
     * @param <T>       the type that list holds
     */
    public static <T> void convertToNonEmptyList(List<T> inputList) {
        boolean isNullOrEmpty = (inputList == null || inputList.isEmpty());
        if (isNullOrEmpty) {
            inputList = new ArrayList<>();
        }
    }

    /**
     * Checks if the list is emtpy and provided the message if so
     *
     * @param list    the list to check
     * @param message the message if it was empty
     */
    public static void requiresNonNull(List<LocalDateTime> list, String message) {
        if (list == null || list.isEmpty()) {
            throw new NullPointerException(message);
        }
    }
}
