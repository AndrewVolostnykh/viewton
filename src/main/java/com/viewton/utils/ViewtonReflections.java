package com.viewton.utils;

import com.viewton.lang.AvgAlias;
import com.viewton.lang.SumAlias;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewtonReflections {

    /**
     * This method processes a list of attribute names and checks if any of the corresponding fields in the provided entity class
     * are annotated with {@link AvgAlias}. If a field is annotated with {@code AvgAlias}, the method retrieves the value of the
     * {@code mapTo} attribute from the annotation and adds it to the result list. If the field is not annotated with {@code AvgAlias},
     * the method simply adds the attribute name to the result list as is.
     *
     * <p>This is useful in scenarios where certain fields in an entity are mapped to other fields (for example, fields representing
     * the result of an {@code avg} operation), and we need to dynamically determine the mapping of these attributes.
     *
     * <p>Example usage:
     * <pre>
     * {@code
     * List<String> attributes = Arrays.asList("randomNumber", "someOtherField");
     * List<String> avgAliases = getAvgAliases(attributes, SomeEntity.class);
     * }
     * </pre>
     *
     * @param attributes A list of attribute names (field names) to check against the entity class.
     * @param entity The class of the entity to examine for fields annotated with {@code AvgAlias}.
     *
     * @return A list of attribute names where each name is either the original attribute name or the name specified in the
     *         {@code mapTo} attribute of the {@code AvgAlias} annotation if the field is annotated.
     */
    public static List<String> getAvgAliases(List<String> attributes, Class<?> entity) {
        List<String> result = new ArrayList<>();
        Map<String, Field> fields = mapDeclaredFields(entity);
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

    public static List<String> getSumAliases(List<String> attributes, Class<?> entity) {
        List<String> result = new ArrayList<>();
        Map<String, Field> fields = mapDeclaredFields(entity);
        for (String attribute : attributes) {
            Field field = fields.get(attribute);
            if (field.isAnnotationPresent(SumAlias.class)) {
                SumAlias declaredAnnotation = field.getDeclaredAnnotation(SumAlias.class);
                result.add(declaredAnnotation.mapTo());
            } else {
                result.add(attribute);
            }
        }

        return result;
    }

    private static Map<String, Field> mapDeclaredFields(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Function.identity()));
    }
}
