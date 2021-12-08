package com.bringframework.util;

import com.bringframework.definition.BoboDefinition;
import lombok.experimental.UtilityClass;

import static java.beans.Introspector.decapitalize;

@UtilityClass
public class BoboDefinitionUtil {

    public BoboDefinition buildDefinition(Class<?> type) {
        return buildDefinition(type, generateBoboName(type));
    }

    public BoboDefinition buildDefinition(Class<?> type, String name) {
        return BoboDefinition.builder()
                .boboName(name)
                .boboClass(type)
                .build();
    }

    public BoboDefinition buildDefinition(Class<?> type, String name, String configMethodName, Class<?> configClassName) {
        return BoboDefinition.builder()
                .boboName(name)
                .boboClass(type)
                .configurationBoboName(decapitalize(configClassName.getSimpleName()))
                .configurationMethodName(configMethodName)
                .build();
    }

    public static String generateBoboName(Class<?> type) {
        return decapitalize(type.getSimpleName());
    }
}
