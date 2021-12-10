package com.bringframework.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionErrorMessage {
    public static final String BOBO_INSTANTIATION_EXCEPTION = "Cannot instantiate bobo: '%s'";
    public static final String NO_SUCH_BOBO_DEFINITION_BY_TYPE_EXCEPTION = "No such bobo definition of type '%s'";
    public static final String NO_SUCH_BOBO_DEFINITION_BY_NAME_EXCEPTION = "No such bobo definition with name '%s'";
    public static final String AMBIGUOUS_BOBO_EXCEPTION = "No qualifying bobo of type '%s' available: expected single matching bobo but found %d: %s";
    public static final String INVALID_INJECT_MARKED_CONSTRUCTOR_EXCEPTION = "Error creating bobo with name '%s': Invalid inject-marked constructor: %s. Found constructor with 'required' Inject annotation already: %s";
}
