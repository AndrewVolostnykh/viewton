package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.utils.ThreeArgsFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class RawWhereClauseInstance {

    public static BiFunction<String, String, RawWhereClause> instantiate = RawWhereClause::new;
}
