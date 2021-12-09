package com.bringframework.util;

import com.bringframework.annotation.Inject;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.bringframework.exception.ExceptionErrorMessage.INVALID_INJECT_MARKED_CONSTRUCTOR_EXCEPTION;
import static java.beans.Introspector.decapitalize;

@UtilityClass
public class BoboDefinitionUtil {

    public BoboDefinition buildDefinition(Class<?> type) {
        return buildDefinition(type, generateBoboName(type));
    }

    public BoboDefinition buildDefinition(Class<?> type, String name) {
        Constructor<?> constructor = defineConstructor(type, name);
        return BoboDefinition.builder()
                .boboName(name)
                .boboClass(type)
                .constructor(constructor)
                .build();
    }

    public BoboDefinition buildDefinition(Class<?> type, String name, Method configMethod, Class<?> configClassName) {
        return BoboDefinition.builder()
                .boboName(name)
                .boboClass(type)
                .configurationBoboName(decapitalize(configClassName.getSimpleName()))
                .configurationMethod(configMethod)
                .build();
    }

    public static String generateBoboName(Class<?> type) {
        return decapitalize(type.getSimpleName());
    }

    private Constructor<?> defineConstructor(Class<?> type, String name) {
        Constructor<?> defaultConstructor = null;
        Constructor<?> injectAnnotatedConstructor = null;
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                defaultConstructor = constructor;
                continue;
            }
            if (constructor.isAnnotationPresent(Inject.class) && injectAnnotatedConstructor != null) {
                throw new BoboException(String.format(
                        INVALID_INJECT_MARKED_CONSTRUCTOR_EXCEPTION, name, injectAnnotatedConstructor, constructor));
            }
            if (constructor.isAnnotationPresent(Inject.class)) {
                injectAnnotatedConstructor = constructor;
            }
        }
        if (injectAnnotatedConstructor != null) {
            return injectAnnotatedConstructor;
        }
        if (defaultConstructor != null) {
            return defaultConstructor;
        }
        throw new BoboException("No default constructor found for type " + type);
    }

}
