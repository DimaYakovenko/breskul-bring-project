package com.bringframework.configurator;

import com.bringframework.BoboFactory;
import com.bringframework.annotation.BoboValue;
import com.bringframework.exception.BoboException;
import com.bringframework.util.TypeParserUtil;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class BoboValueAnnotationConfiguration implements BoboConfigurator {

    private final Map<String, String> propertiesMap;

    @SneakyThrows
    public BoboValueAnnotationConfiguration() {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        propertiesMap = lines.map(line -> line.trim().split("=")).collect(toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));

    }


    @Override
    public void configure(Object bobo, BoboFactory registry) {
        for (Field field : bobo.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BoboValue.class)) {
                var annotation = field.getAnnotation(BoboValue.class);
                Object value = annotation.value().isEmpty()
                        ? propertiesMap.get(field.getName())
                        : propertiesMap.get(annotation.value());
                value = TypeParserUtil.parseToType(value, field.getType());
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
}
