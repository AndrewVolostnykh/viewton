package andrew.volostnykh.viewton.type;

import andrew.volostnykh.viewton.ComparableValue;
import andrew.volostnykh.viewton.RawValue;

import java.util.stream.Stream;

public class EnumConverter implements RawToJavaTypeConverter {
    @Override
    public boolean requiredType(Class<?> javaType) {
        return Enum.class.isAssignableFrom(javaType);
    }

    @Override
    public ComparableValue convert(RawValue rawValue) {
        return new ComparableValue(
                getEnum(rawValue.getJavaType(), rawValue.getValue()),
                rawValue.isIgnoreCase()
        );
    }

    private Comparable getEnum(Class<?> javaType, String fieldValue) {
        return Stream.of(javaType.getEnumConstants())
                .filter(enumValue -> enumValue.toString().equals(fieldValue))
                .map(Comparable.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Undefined enum value: " + fieldValue));
    }
}
