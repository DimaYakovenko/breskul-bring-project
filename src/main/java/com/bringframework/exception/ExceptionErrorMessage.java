package com.bringframework.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionErrorMessage {
    public static final String BOBO_INSTANTIATION_ERROR = "Cannot instantiate bobo: '%s'";
    public static final String NO_SUCH_BOBO_DEFINIITON_ERROR = "No such bobo definition of type '%s'";
    public static final String AMBIGUOUS_BOBO_ERROR = "No qualifying bobo of type '%s' available: expected single matching bobo but found %d: %s";
}
