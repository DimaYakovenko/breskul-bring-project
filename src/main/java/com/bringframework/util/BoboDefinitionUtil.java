package com.bringframework.util;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.InitMethod;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.bringframework.exception.ExceptionErrorMessage.INVALID_INJECT_MARKED_CONSTRUCTOR_EXCEPTION;
import static java.beans.Introspector.decapitalize;

/**
 * Class for parsing {@link BoboDefinition} from the Class type
 *
 * @author Mykhailo Pysarenko
 * @since 8 december 2021
 */
@UtilityClass
public class BoboDefinitionUtil {

    /**
     * Build {@link BoboDefinition}
     *
     * @param type
     * @return {@link BoboDefinition}
     */
    public BoboDefinition buildDefinition(Class<?> type) {
        return buildDefinition(type, generateBoboName(type));
    }

    /**
     * Build {@link BoboDefinition}
     *
     * @param type Class to be parsed
     * @param name String boboName
     * @return {@link BoboDefinition}
     */
    public BoboDefinition buildDefinition(Class<?> type, String name) {
        Constructor<?> constructor = defineConstructor(type, name);
        return BoboDefinition.builder()
                .boboName(name)
                .boboClass(type)
                .initMethodName(getInitMethodName(type))
                .constructor(constructor)
                .build();
    }

    /**
     * Build {@link BoboDefinition} for {@link com.bringframework.annotation.Bobo}
     *
     * @param type         Class to be parsed
     * @param name         String boboName
     * @param configMethod Configuration method
     * @param configClass  Configuration class
     * @return {@link BoboDefinition}
     */
    public BoboDefinition buildDefinition(Class<?> type, String name, Method configMethod, Class<?> configClass, String initMethodName) {
        return BoboDefinition.builder()
                .boboName(name)
                .boboClass(type)
                .configurationBoboName(decapitalize(configClass.getSimpleName()))
                .configurationMethod(configMethod)
                .initMethodName(initMethodName)
                .build();
    }

    /**
     * Generate Bobo name from class
     *
     * @param type
     * @return bobo name
     */
    public static String generateBoboName(Class<?> type) {
        return decapitalize(type.getSimpleName());
    }

    private static String getInitMethodName(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(InitMethod.class))
                .map(Method::getName)
                .findFirst()
                .orElse(null);
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
