package com.bringframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method produces a bobo to be managed by the registry.
 * <p>
 * Default name for bob is method name. It is possible to set up custom name by annotation attribute name().
 * Be aware, that class should be marked as @Configuration to enable this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bobo {

    /**
     * The value may indicate a suggestion for a logical bobo name
     *
     * @return the suggested bobo name, if any (or method name otherwise)
     */
    String name() default "";

    /**
     * The optional name of a method to call on the bean instance during initialization.
     * Not commonly used, given that the method may be called programmatically directly
     * within the body of a Bean-annotated method.
     * <p>The default value is {@code ""}, indicating no init method to be called.
     */
    String initMethod() default "";

}
