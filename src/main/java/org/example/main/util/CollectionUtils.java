package org.example.main.util;

import java.util.List;

public class CollectionUtils {

    public static <T> List<T> nonEmptyListConvertor(List<T> inputList) {
        boolean isNullOrEmpty = (inputList == null || inputList.isEmpty());
        return isNullOrEmpty ? List.of() : inputList;
    }

}
