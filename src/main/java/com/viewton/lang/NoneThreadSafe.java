package com.viewton.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker for classes and methods which is not thread-safe.
 * Marked classes and methods should not be used in concurrent environment.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface NoneThreadSafe {
}
