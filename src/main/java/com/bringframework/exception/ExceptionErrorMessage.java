package com.bringframework.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionErrorMessage {
    public static final String BOBO_INSTANTIATION_EXCEPTION = "Cannot instantiate bobo: '%s'";
    public static final String NO_SUCH_BOBO_DEFINIITON_EXCEPTION_BY_TYPE = "No such bobo definition of type '%s'";
    public static final String NO_SUCH_BOBO_DEFINIITON_EXCEPTION_BY_NAME = "No such bobo definition with name '%s'";
    public static final String AMBIGUOUS_BOBO_EXCEPTION = "No qualifying bobo of type '%s' available: expected single matching bobo but found %d: %s";
}
