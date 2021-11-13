package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import com.bringframework.exception.BoboException;
import com.bringframework.util.BoboDefinitionUtil;

import java.lang.reflect.Method;

public class BoboAnnotationConfigurator implements BoboConfigurator{

    @Override
    public void configure(Object bobo, BoboRegistry registry) {
        if (bobo.getClass().isAnnotationPresent(Configuration.class)) {
            for (Method method : bobo.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Bobo.class)) {
                    registerBobo(bobo, registry, method);
                }
            }
        }
    }

    private void registerBobo(Object bobo, BoboRegistry registry, Method method) {
        try {
            Bobo annotation = method.getAnnotation(Bobo.class);
            String boboName = annotation.name().isEmpty()
                    ? BoboDefinitionUtil.generateBoboName(method.getReturnType())
                    : annotation.name();
            Object createdBobo = method.invoke(bobo);
            registry.register(createdBobo.getClass(), createdBobo, boboName);
        } catch (Exception e) {
            throw new BoboException("Can't create an object of " + bobo + " class", e);
        }
    }
}
