package com.bringframework.configurator;

import com.bringframework.BoboFactory;
import com.bringframework.annotation.BoboValue;
import com.bringframework.exception.BoboException;
import com.bringframework.util.TypeResolverUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class BoboValueAnnotationConfiguration implements BoboConfigurator {

    private final Map<String, String> propertiesMap;

    private final String KEY_VALUE_DELIMITER = "=";

    private final String DEFAULT_PROPERTIES_FILE_NAME = "application.properties";

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
                String value = annotation.value().isEmpty()
                        ? propertiesMap.get(field.getName())
                        : propertiesMap.get(annotation.value());

                try {
                    field.setAccessible(true);
                    field.set(bobo, TypeResolverUtil.parseToType(value, field.getType()));
                } catch (IllegalAccessException e) {
                    throw new BoboException(String.format("Can't set value: %s from properties to field: %s in class: %s",
                            value, field.getName(), bobo.getClass().getName()), e);
                }
            }
        }
    }

    private List<String> findAvailableProperties() {
        return Optional.ofNullable(ClassLoader.getSystemClassLoader().getResource(DEFAULT_PROPERTIES_FILE_NAME))
                .map(URL::getPath)
                .map(this::readPropertiesByPath)
                .orElse(Collections.emptyList());
    }

    private List<String> readPropertiesByPath(String path) {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(toList());
        } catch (FileNotFoundException e) {
            throw new BoboException("Can't find properties file in resources with name application.properties", e);
        } catch (IOException e) {
            throw new BoboException("Can't read properties file", e);
        }
    }
}
