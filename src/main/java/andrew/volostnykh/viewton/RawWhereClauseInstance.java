package andrew.volostnykh.viewton;

import andrew.volostnykh.viewton.lang.NoneThreadSafe;

import java.util.function.BiFunction;

/**
 * This class provides a mechanism to instantiate {@link RawWhereClause} objects.
 * It contains a static {@link BiFunction} that is responsible for creating instances of
 * {@link RawWhereClause} based on the field name and raw condition from the query parameters.
 *
 * <p>This class allows for flexibility in case the {@link RawWhereClause} needs to be extended
 * in an end project. By changing the {@link BiFunction} implementation, you can replace the
 * default instantiation logic to instantiate a subclass of {@link RawWhereClause} instead.</p>
 *
 * <p>By default, the {@link BiFunction} creates a new instance of {@link RawWhereClause},
 * but it can be customized if the project requires additional functionality or different
 * behavior for the `RawWhereClause` instantiation.</p>
 */
public class RawWhereClauseInstance {

    @NoneThreadSafe
    public static BiFunction<String, String, RawWhereClause> instantiate = RawWhereClause::new;
}
