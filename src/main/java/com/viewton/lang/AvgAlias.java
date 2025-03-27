package com.viewton.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code AvgAlias} annotation is used to specify the field where the result of the {@code avg} operation will be stored.
 * This annotation is required when the field in the entity has a type that does not support floating-point values (e.g., {@link Long}),
 * while the {@code avg} operation returns a result with a floating-point type (e.g., {@link Double}).
 * <p>
 * This annotation allows you to specify which field will hold the result of the {@code avg} operation, ensuring the correct type mapping.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @AvgAlias(mapTo = "randomNumberResult")
 * @Column(name = "RANDOM_NUMBER")
 * private Long randomNumber;
 *
 * @Transient
 * private Double randomNumberResult;
 * }
 * </pre>
 * In this example, the result of the average calculation for the field {@code RANDOM_NUMBER} will be stored in the {@code randomNumberResult} field.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AvgAlias {

    /**
     * Specifies the name of the field where the result of the {@code avg} operation will be stored.
     * This field should be of a floating-point type (e.g., {@link Double}),
     * as the result of {@code avg} is typically a floating-point value.
     *
     * @return the name of the field where the result will be saved.
     */
    String mapTo();
}