package andrew.volostnykh.viewton;

import lombok.Data;

@Data
public class RawValue {
    private String value;
    private boolean ignoreCase = false;
    private Class<?> javaType;
}
