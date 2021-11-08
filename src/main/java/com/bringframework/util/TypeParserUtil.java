package com.bringframework.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class TypeParserUtil {

    public static Object parseToType(Object value, Class<?> clazz) {
        if (!(value instanceof String)) return value;
        String strValue = (String) value;
        if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(strValue);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.parseLong(strValue);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(strValue);
        } else if (clazz == float.class || clazz == Float.class) {
            return Float.parseFloat(strValue);
        } else if (clazz == byte.class || clazz == Byte.class) {
            return Byte.parseByte(strValue);
        } else if (clazz == short.class || clazz == Short.class) {
            return Short.parseShort(strValue);
        } else if (clazz == BigInteger.class) {
            return new BigInteger(strValue);
        } else if (clazz == BigDecimal.class) {
            return new BigDecimal(strValue);
        }
        return value;
    }
}
