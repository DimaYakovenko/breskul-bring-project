package com.bringframework.configurator;

import com.bringframework.BoboFactory;
import com.bringframework.annotation.BoboValue;
import com.bringframework.util.TypeParserUtil;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class PropertyValueAnnotationBoboConfigurator implements BoboConfigurator {

    private final Map<String, String> propertiesMap;

    @SneakyThrows
    public PropertyValueAnnotationBoboConfigurator() {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        propertiesMap = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));

    }

    @SneakyThrows
    @Override
    public void configure(Object bobo, BoboFactory registry) {
        for (Field field : bobo.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BoboValue.class)) {
                var annotation = field.getAnnotation(BoboValue.class);
                Object value = annotation.value().isEmpty() ? propertiesMap.get(field.getName()) : propertiesMap.get(annotation.value());
                value = TypeParserUtil.parseToType(value, field.getType());
                field.setAccessible(true);
                field.set(bobo, value);
            }
        }
    }
}
