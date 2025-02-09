package andrew.volostnykh.viewton;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Operator {

    LESS_OR_EQUAL("<="),
    GREATER_OR_EQUAL(">="),
    NOT_EQUAL("<>"),
    LESS("<"),
    GREATER(">"),
    RANGE(".."),
    OR("|"),
    EQUAL("");

    @Getter
    private final String value;

}
