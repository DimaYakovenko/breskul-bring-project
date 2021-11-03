package com.bringframework.configurator;

import com.bringframework.BoboFactory;
import com.bringframework.annotation.Inject;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class InjectAnnotationBoboConfigurator implements BoboConfigurator {
    @Override
    @SneakyThrows
    public void configure(Object bobo, BoboFactory registry) {
        for (Field field : bobo.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object object = registry.getBobo(field.getType());
                field.set(bobo, object);
            }
        }
    }
}
