package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.annotation.Inject;
import com.bringframework.exception.BoboException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * <p> BoboConfigurator class for injecting Bobo into fields annotated with {@link Inject}
 * <p>Implementation of {@link BoboConfigurator} interface.
 *
 * @author Andrii Bobrov
 * @author Yuliia Smerechynska
 * @since 12 november 2021
 **/
@Slf4j
public class InjectAnnotationBoboConfigurator implements BoboConfigurator {
    /**
     * If input class has fields annotated with {@link Inject} they will be injected by type.
     *
     * @param bobo     bobo instance for configuring
     * @param registry {@link BoboRegistry}
     */
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
