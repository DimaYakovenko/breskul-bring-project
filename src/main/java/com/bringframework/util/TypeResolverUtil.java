package com.bringframework.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeResolverUtil {

    public static Object parseToType(String propertyValue, Class<?> clazz) {

        if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(propertyValue);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.parseLong(propertyValue);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(propertyValue);
        } else if (clazz == float.class || clazz == Float.class) {
            return Float.parseFloat(propertyValue);
        } else if (clazz == byte.class || clazz == Byte.class) {
            return Byte.parseByte(propertyValue);
        } else if (clazz == short.class || clazz == Short.class) {
            return Short.parseShort(propertyValue);
        } else if (clazz == BigInteger.class) {
            return new BigInteger(propertyValue);
        } else if (clazz == BigDecimal.class) {
            return new BigDecimal(propertyValue);
        }
        return propertyValue;
    }
}
