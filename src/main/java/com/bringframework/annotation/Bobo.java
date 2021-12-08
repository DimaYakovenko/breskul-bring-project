package com.bringframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method produces a bobo to be managed by the registry.
 *
 * Default name for bob is method name. It is possible to set up custom name by annotation attribute name().
 * Be aware, that class should be marked as @Configuration to enable this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bobo {

    /**
     * The value may indicate a suggestion for a logical bobo name
     * @return the suggested bobo name, if any (or method name otherwise)
     */
    String name() default "";
}
