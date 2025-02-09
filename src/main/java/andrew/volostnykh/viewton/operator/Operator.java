package andrew.volostnykh.viewton.operator;

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
