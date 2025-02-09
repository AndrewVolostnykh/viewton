package andrew.volostnykh.viewton.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Custom annotation to enable Viewton functionality in a Spring application.

 * <p>Usage:</p>
 * <pre>
 * @EnableViewton
 * public class SomeSpringConfiguration {
 *     // Additional Spring configuration
 * }
 * </pre>
 *
 * <p>When this annotation is applied to a class, Spring will automatically import the
 * `ViewtonConfiguration` class and all the associated beans and configurations will
 * be registered in the Spring context.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ViewtonConfiguration.class)
public @interface EnableViewton {

}