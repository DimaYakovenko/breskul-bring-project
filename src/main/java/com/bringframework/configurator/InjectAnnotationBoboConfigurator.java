package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.annotation.Inject;
import com.bringframework.exception.BoboException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class InjectAnnotationBoboConfigurator implements BoboConfigurator {
    @Override
    public void configure(Object bobo, BoboRegistry registry) {
        try {
            log.debug("Starting fetching declared fields for object {}", bobo);
            for (Field field : bobo.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    Object object = registry.getBobo(field.getType());
                    field.set(bobo, object);
                }
            }
        } catch (BoboException boboException) {
            log.error("Error during configuring bobo object", boboException);
            throw boboException;
        } catch (Exception e) {
            log.error("Error during configuring bobo object", e);
            throw new BoboException("Error during configuring bobo object", e);
        }
    }
}
