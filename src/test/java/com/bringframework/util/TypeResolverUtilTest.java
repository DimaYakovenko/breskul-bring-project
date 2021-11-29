package com.bringframework.util;

import items.model.FakeUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.bringframework.util.TypeResolverUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class TypeResolverUtilTest {

    private final BigInteger BIG_INTEGER_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    private final BigDecimal BIG_DECIMAL_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);
    private final String TEST_BIG_INTEGER_VALUE = String.valueOf(BIG_INTEGER_VALUE);
    private final String TEST_BIG_DECIMAL_VALUE = String.valueOf(BIG_DECIMAL_VALUE);

    private final String TEST_INT_VALUE = String.valueOf(Integer.MAX_VALUE - 1);
    private final String TEST_LONG_VALUE = String.valueOf(Long.MAX_VALUE - 1);
    private final String TEST_DOUBLE_VALUE = String.valueOf(Double.MAX_VALUE - 1);
    private final String TEST_FLOAT_VALUE = String.valueOf(Float.MAX_VALUE - 1);
    private final String TEST_BYTE_VALUE = String.valueOf(Byte.MAX_VALUE - 1);
    private final String TEST_SHORT_VALUE = String.valueOf(Short.MAX_VALUE - 1);
    private final String TEST_STRING_VALUE = "TEST VALUE";
    private final String CLASS_CAST_EXCEPTION_MSG = "Can't cast value \"TEST VALUE\" to FakeUser class";

    @Test
    void checkTypeCast_successfulCase() {

        assertEquals(Integer.MAX_VALUE - 1, parseToType(TEST_INT_VALUE, int.class));
        assertEquals(Integer.MAX_VALUE - 1, parseToType(TEST_INT_VALUE, Integer.class));

        assertEquals(Long.MAX_VALUE - 1, parseToType(TEST_LONG_VALUE, long.class));
        assertEquals(Long.MAX_VALUE - 1, parseToType(TEST_LONG_VALUE, Long.class));

        assertEquals(Double.MAX_VALUE - 1, parseToType(TEST_DOUBLE_VALUE, double.class));
        assertEquals(Double.MAX_VALUE - 1, parseToType(TEST_DOUBLE_VALUE, Double.class));

        assertEquals(Float.MAX_VALUE - 1, parseToType(TEST_FLOAT_VALUE, float.class));
        assertEquals(Float.MAX_VALUE - 1, parseToType(TEST_FLOAT_VALUE, Float.class));

        assertEquals(Byte.parseByte(TEST_BYTE_VALUE), parseToType(TEST_BYTE_VALUE, byte.class));
        assertEquals(Byte.parseByte(TEST_BYTE_VALUE), parseToType(TEST_BYTE_VALUE, Byte.class));

        assertEquals(Short.parseShort(TEST_SHORT_VALUE), parseToType(TEST_SHORT_VALUE, short.class));
        assertEquals(Short.parseShort(TEST_SHORT_VALUE), parseToType(TEST_SHORT_VALUE, Short.class));

        assertEquals(BIG_INTEGER_VALUE, parseToType(TEST_BIG_INTEGER_VALUE, BigInteger.class));
        assertEquals(BIG_DECIMAL_VALUE, parseToType(TEST_BIG_DECIMAL_VALUE, BigDecimal.class));
        assertSame(TEST_STRING_VALUE, parseToType(TEST_STRING_VALUE, String.class));
    }

    @Test
    void checkTypeCast_failCase() {

        Exception numberFormatException = assertThrows(NumberFormatException.class,
                () -> parseToType(TEST_LONG_VALUE, int.class));

        Exception classCastException = assertThrows(ClassCastException.class,
                () -> parseToType(TEST_STRING_VALUE, FakeUser.class));

        assertSame(NumberFormatException.class, numberFormatException.getClass());
        assertSame(ClassCastException.class, classCastException.getClass());
        assertEquals(CLASS_CAST_EXCEPTION_MSG, classCastException.getMessage());
    }
}
