package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.annotation.Inject;
import com.bringframework.exception.BoboException;

import java.lang.reflect.Field;

public class InjectAnnotationBoboConfigurator implements BoboConfigurator {
    @Override
    public void configure(Object bobo, BoboRegistry registry) {
        try {
            for (Field field : bobo.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    Object object = registry.getBobo(field.getType());
                    field.set(bobo, object);
                }
            }
        } catch (BoboException boboException) {
            throw boboException;
        } catch (Exception e) {
            throw new BoboException("Error during configuring bobo object", e);
        }
    }
}
