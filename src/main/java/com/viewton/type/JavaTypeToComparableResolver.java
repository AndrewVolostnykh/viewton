package com.viewton.type;

import com.viewton.ComparableValue;
import com.viewton.RawValue;
import com.viewton.lang.NoneThreadSafe;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class responsible for resolving database types to their corresponding Java types and converting them
 * into {@link ComparableValue} objects. This resolver provides a mechanism for converting various raw database
 * values into Java-compatible types that can be used in comparisons or operations.
 * <p>
 * The class maintains a list of {@link RawToJavaTypeConverter} instances that handle specific conversions
 * for different types of database values. The registered converters are used to convert raw values (such as from
 * a database query result) into Java values of the appropriate type (e.g., Integer, String, BigDecimal, etc.).
 * </p>
 * <p>
 * The class also allows for registering custom converters if there are additional data types or conversions
 * needed.
 * </p>
 */
public class JavaTypeToComparableResolver {

    private static final List<RawToJavaTypeConverter> DATA_TYPE_CONVERTERS = new ArrayList<>();

    static {
        DATA_TYPE_CONVERTERS.add(new BooleanConverter());
        DATA_TYPE_CONVERTERS.add(new ShortConverter());
        DATA_TYPE_CONVERTERS.add(new IntegerConverter());
        DATA_TYPE_CONVERTERS.add(new LongConverter());
        DATA_TYPE_CONVERTERS.add(new FloatConverter());
        DATA_TYPE_CONVERTERS.add(new DoubleConverter());
        DATA_TYPE_CONVERTERS.add(new BigIntegerConverter());
        DATA_TYPE_CONVERTERS.add(new BigDecimalConverter());
        DATA_TYPE_CONVERTERS.add(new LocalDateConverter());
        DATA_TYPE_CONVERTERS.add(new LocalDateTimeConverter());
        DATA_TYPE_CONVERTERS.add(new EnumConverter());
    }

    /**
     * Registers a custom converter to handle the conversion of raw database values into Java types.
     *
     * @param converter The {@link RawToJavaTypeConverter} to register.
     * @see RawToJavaTypeConverter
     */
    @NoneThreadSafe
    public static void registerConverter(RawToJavaTypeConverter converter) {
        DATA_TYPE_CONVERTERS.add(converter);
    }

    /**
     * Remove data type converter from list by index.
     * It is important to remove by index because in some cases
     * order of converters in list is important.
     */
    @NoneThreadSafe
    public static void removeConverter(int index) {
        DATA_TYPE_CONVERTERS.remove(index);
    }

    /**
     * Converts a {@link RawValue} into a {@link ComparableValue} by finding the appropriate converter based
     * on the raw value's Java type.
     * <p>
     * If a suitable converter is found in the list of registered converters, the raw value will be converted
     * to a {@link ComparableValue}. If no converter is found for the raw type, the method will return a
     * default {@link ComparableValue} using the raw value's value and ignore case flag.
     * </p>
     *
     * @param rawValue The raw database value to be converted.
     * @return The corresponding {@link ComparableValue} for the raw database value.
     */
    public static ComparableValue toJavaComparable(RawValue rawValue) {
        return DATA_TYPE_CONVERTERS.stream()
                .filter(converter -> converter.requiredType(rawValue.getJavaType()))
                .findFirst()
                .map(converter -> converter.convert(rawValue))
                .orElse(new ComparableValue(rawValue.getValue(), rawValue.isIgnoreCase()));
    }

}
