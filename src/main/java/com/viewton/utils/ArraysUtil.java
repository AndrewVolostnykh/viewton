package com.viewton.utils;

import java.util.Arrays;
import java.util.List;

public class ArraysUtil {

    public static <T> List<T> asListSafe(T[] array) {
        if (array == null) {
            return null;
        }

        return Arrays.asList(array);
    }
}
