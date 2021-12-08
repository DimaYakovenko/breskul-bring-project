package com.bringframework.definition;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import com.bringframework.util.BoboDefinitionUtil;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationAnnotationBoboDefinitionScanner {

    public static List<BoboDefinition> scan(String... basePackages) {
        Set<Class<?>> configurations = new Reflections(basePackages, Scanners.TypesAnnotated)
                .getTypesAnnotatedWith(Configuration.class);

        return configurations.stream()
                .map(BoboDefinitionUtil::buildDefinition)
                .collect(Collectors.toList());
    }

    public static List<BoboDefinition> createDefinitionsByConfiguration(List<BoboDefinition> configurations) {
        List<BoboDefinition> resultDefinition = new ArrayList<>();

        for (BoboDefinition configDefinition : configurations) {
            Class<?> configClass = configDefinition.getBoboClass();
            Method[] declaredMethods = configClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(Bobo.class)) {
                    String boboName = findBoboName(method);
                    BoboDefinition boboDefinition = BoboDefinitionUtil.buildDefinition(method.getReturnType(), boboName,
                            method.getName(), configClass.getName());
                    resultDefinition.add(boboDefinition);
                }
            }
        }
        return resultDefinition;
    }

    private static String findBoboName(Method method) {
        Bobo annotation = method.getAnnotation(Bobo.class);
        return annotation.name().isEmpty()
                ? method.getName()
                : annotation.name();
    }
}
