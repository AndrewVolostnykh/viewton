package com.viewton.utils;

import com.viewton.lang.AvgAlias;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewtonReflections {

    /**
     * Method for getting Avg aliases from java class.
     * Iterating through attributes searches for a fields in entity and replacing aliases if
     * it is annotated with {@link com.viewton.lang.AvgAlias}.
     */
    public static List<String> getAvgAliases(List<String> attributes, Class<?> entity) {
        List<String> result = new ArrayList<>();
        Map<String, Field> fields = Arrays.stream(entity.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        for (String attribute : attributes) {
            Field field = fields.get(attribute);
            if (field.isAnnotationPresent(AvgAlias.class)) {
                AvgAlias declaredAnnotation = field.getDeclaredAnnotation(AvgAlias.class);
                result.add(declaredAnnotation.mapTo());
            } else {
                result.add(attribute);
            }
        }

        return result;
    }
}
