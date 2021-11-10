package com.bringframework.configurator;

import com.bringframework.BoboFactory;
import com.bringframework.annotation.BoboValue;
import com.bringframework.exception.BoboException;
import com.bringframework.util.TypeResolverUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class BoboValueAnnotationConfiguration implements BoboConfigurator {

    private final Map<String, String> propertiesMap;

    private final String LINE_SPLIT_REGEX = "=";

    public BoboValueAnnotationConfiguration() {
        propertiesMap = findAvailableProperties()
                .map(line -> line.split(LINE_SPLIT_REGEX))
                .collect(toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));
    }

    @Override
    public void configure(Object bobo, BoboFactory registry) {
        for (Field field : bobo.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BoboValue.class)) {
                var annotation = field.getAnnotation(BoboValue.class);
                Object value = annotation.value().isEmpty()
                        ? propertiesMap.get(field.getName())
                        : propertiesMap.get(annotation.value());
                value = TypeResolverUtil.parseToType(value, field.getType());
                try {
                    field.setAccessible(true);
                    field.set(bobo, value);
                } catch (IllegalAccessException e) {
                    throw new BoboException(String.format("Can't set value: %s from properties to field: %s in class: %s",
                            value, field.getName(), bobo.getClass().getName()), e);
                }
            }
        }
    }

    private Stream<String> findAvailableProperties() {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        if (path != null) {
            return readPropertiesByPath(path);
        }
        return Stream.empty();
    }

    private Stream<String> readPropertiesByPath(String path) {
        try {
            return new BufferedReader(new FileReader(path)).lines();
        } catch (FileNotFoundException e) {
            throw new BoboException("Can't find properties file in resources with name application.properties", e);
        }
    }
}
