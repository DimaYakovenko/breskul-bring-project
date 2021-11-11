package com.bringframework.configurator;

import com.bringframework.BoboFactory;
import com.bringframework.annotation.BoboValue;
import com.bringframework.exception.BoboException;
import com.bringframework.util.TypeResolverUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class BoboValueAnnotationConfiguration implements BoboConfigurator {

    private final Map<String, String> propertiesMap;

    private final String KEY_VALUE_DELIMITER = "=";

    public BoboValueAnnotationConfiguration() {
        propertiesMap = findAvailableProperties()
                .stream()
                .map(line -> line.split(KEY_VALUE_DELIMITER))
                .collect(toMap(key -> key[0].trim(), value -> value[1].trim()));
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

    private List<String> findAvailableProperties() {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        if (path != null) {
            return readPropertiesByPath(path).collect(Collectors.toList());
        }
        return List.of();
    }

    private Stream<String> readPropertiesByPath(String path) {
        try {
            return new BufferedReader(new FileReader(path)).lines();
        } catch (FileNotFoundException e) {
            throw new BoboException("Can't find properties file in resources with name application.properties", e);
        }
    }
}
