package andrew.volostnykh.viewton.operator;

/**
 * Abstract base class representing an operator used for querying and filtering in requests.
 * <p>
 * Operators are typically used to perform specific operations (like comparison or range checking)
 * on fields in query parameters. This class encapsulates the operator value and provides common functionality
 * for all operator types.
 * </p>
 */
public abstract class Operator {

    private final String value;

    public Operator(String value) {
        this.value = value;
    }

    public boolean contains(String condition) {
        return condition.contains(value);
    }

    public String getValue() {
        return value;
    }
}
