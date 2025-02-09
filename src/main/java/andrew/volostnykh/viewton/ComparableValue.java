package andrew.volostnykh.viewton;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComparableValue {
    private Comparable value;
    private boolean ignoreCase;
}
