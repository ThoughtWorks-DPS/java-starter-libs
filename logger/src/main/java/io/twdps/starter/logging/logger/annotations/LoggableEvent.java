package io.twdps.starter.logging.logger.annotations;

import io.twdps.starter.logging.logger.domain.ApplicationTier;

import java.lang.annotation.*;

import static io.twdps.starter.logging.logger.domain.ApplicationTier.UNKNOWN;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LoggableEvent {

    ApplicationTier applicationTier() default UNKNOWN;

    String action() default "UNKNOWN";
}
