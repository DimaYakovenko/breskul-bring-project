package com.bringframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation used at the field parameter level that indicates a default value expression for the annotated element.
 *
 *  Read value from application.properties file and inject into annotated field.
 *  Can read only int, long, double, float, byte, short and all their wrappers, String, BigInteger and  BigDecimal
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoboValue {
    String value() default "";
}
