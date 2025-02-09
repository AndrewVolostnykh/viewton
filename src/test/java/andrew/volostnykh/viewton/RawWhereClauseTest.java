package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.operator.EqualOperator;
import andrew.volostnykh.viewton.operator.GreaterOperator;
import andrew.volostnykh.viewton.operator.GreaterOrEqualOperator;
import andrew.volostnykh.viewton.operator.LessOperator;
import andrew.volostnykh.viewton.operator.LessOrEqualsOperator;
import andrew.volostnykh.viewton.operator.NotEqualOperator;
import andrew.volostnykh.viewton.operator.OrOperator;
import andrew.volostnykh.viewton.operator.RangeOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RawWhereClauseTest {

    @Test
    @DisplayName("equals")
    void rawWhereClause_equals_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), EqualOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("not equals")
    void rawWhereClause_notEquals_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "<>someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), NotEqualOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("equals ignore case")
    void rawWhereClause_equalsIgnoreCase_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "^someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), EqualOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertTrue(rawWhereClause.getValues().get(0).isIgnoreCase());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("or")
    void rawWhereClause_or_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "someValue|someAnother");
        assertEquals(rawWhereClause.getOperator().getClass(), OrOperator.class);
        assertEquals(2, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals("someAnother", rawWhereClause.getValues().get(1).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("ignore case or")
    void rawWhereClause_ignoreCaseOr_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "someValue|^someAnother");
        assertEquals(rawWhereClause.getOperator().getClass(), OrOperator.class);
        assertEquals(2, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals("someAnother", rawWhereClause.getValues().get(1).getValue());
        assertTrue(rawWhereClause.getValues().get(1).isIgnoreCase());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("range")
    void rawWhereClause_range_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "someValue..someAnother");
        assertEquals(rawWhereClause.getOperator().getClass(), RangeOperator.class);
        assertEquals(2, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals("someAnother", rawWhereClause.getValues().get(1).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("greater")
    void rawWhereClause_greater_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", ">someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), GreaterOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("greater or equals")
    void rawWhereClause_greaterOrEquals_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", ">=someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), GreaterOrEqualOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("less")
    void rawWhereClause_less_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "<someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), LessOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }

    @Test
    @DisplayName("less or equals")
    void rawWhereClause_lessOrEquals_parseCorrectly() {
        RawWhereClause rawWhereClause = new RawWhereClause("someField", "<=someValue");
        assertEquals(rawWhereClause.getOperator().getClass(), LessOrEqualsOperator.class);
        assertEquals(1, rawWhereClause.getValues().size());
        assertEquals("someValue", rawWhereClause.getValues().get(0).getValue());
        assertEquals(rawWhereClause.getFieldName(), "someField");
    }
}
