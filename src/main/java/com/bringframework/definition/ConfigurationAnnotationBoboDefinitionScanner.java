package com.bringframework.definition;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import com.bringframework.util.BoboDefinitionUtil;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Utility class for scanning packages to get types annotated
 * with {@link Configuration} annotations and parse their {@link BoboDefinition}
 *
 * @author Mykhailo Pysarenko
 * @since 8 december 2021
 */
@UtilityClass
public class ConfigurationAnnotationBoboDefinitionScanner {

    /**
     * Scan packages and build {@link BoboDefinition} from classes annotated by {@link Configuration}
     *
     * @param basePackages packages to scan
     * @return List of {@link BoboDefinition}
     */
    public static List<BoboDefinition> scan(String... basePackages) {
        Set<Class<?>> configurations = new Reflections(basePackages, Scanners.TypesAnnotated)
                .getTypesAnnotatedWith(Configuration.class);

        List<BoboDefinition> resultDefinition = new ArrayList<>();

        for (Class<?> configClass : configurations) {
            Method[] declaredMethods = configClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(Bobo.class)) {
                    String boboName = findBoboName(method);
                    BoboDefinition boboDefinition = BoboDefinitionUtil.buildDefinition(method.getReturnType(), boboName,
                            method, configClass);
                    resultDefinition.add(boboDefinition);
                }
            }
        }
        resultDefinition.addAll(configurations.stream()
                .map(BoboDefinitionUtil::buildDefinition)
                .collect(toList()));

        return resultDefinition;
    }

    private static String findBoboName(Method method) {
        Bobo annotation = method.getAnnotation(Bobo.class);
        return annotation.name().isEmpty()
                ? method.getName()
                : annotation.name();
    }
}
