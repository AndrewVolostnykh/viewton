package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

import java.util.ArrayList;
import java.util.List;

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

    public static void registerConverter(RawToJavaTypeConverter converter) {
        DATA_TYPE_CONVERTERS.add(converter);
    }

    public static ComparableValue toJavaComparable(RawValue rawValue) {
        return DATA_TYPE_CONVERTERS.stream()
                .filter(converter -> converter.requiredType(rawValue.getJavaType()))
                .findFirst()
                .map(converter -> converter.convert(rawValue))
                .orElse(new ComparableValue(rawValue.getValue(), rawValue.isIgnoreCase()));
    }

}
