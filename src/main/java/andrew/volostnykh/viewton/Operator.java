package andrew.volostnykh.viewton;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Operator {

    EQUAL(""),
    NOT_EQUAL("<>"),
    LESS("<"),
    GREATER(">"),
    LESS_OR_EQUAL("<="),
    GREATER_OR_EQUAL(">="),
    RANGE(".."),
    OR("|");

    @Getter
    private final String value;

}
