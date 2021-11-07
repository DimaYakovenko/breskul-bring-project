package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.bringframework.exception.ExceptionErrorMessage.BOBO_INSTANTIATION_ERROR;

public class BoboFactory {

    private final List<BoboConfigurator> configurators;
    private final BoboRegistry registry;

    public BoboFactory(BoboRegistry registry, String packageToScan) {
        this.registry = registry;
        BoboConfiguratorScanner configuratorScanner = new BoboConfiguratorScanner(packageToScan);
        this.configurators = configuratorScanner.scan();
    }

    public <T> T createBobo(BoboDefinition definition) {
        try {
            T newBobo = instantiate(definition);

            invokeInit(definition, newBobo);

            return newBobo;
        } catch (Exception e) {
            throw new BoboException(String.format(BOBO_INSTANTIATION_ERROR, definition.getBoboName()), e);
        }
    }

    public <T> void configure(T bobo) {
        configurators.forEach(boboConfigurator -> boboConfigurator.configure(bobo, registry));
    }

    private <T> T instantiate(BoboDefinition definition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (T) definition.getBoboClass().getDeclaredConstructor().newInstance();
    }

    private <T> void invokeInit(BoboDefinition definition, T bobo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (definition.getInitMethodName() != null) {
            Method initMethod = definition.getBoboClass().getMethod(definition.getInitMethodName());
            initMethod.invoke(bobo);
        }
    }
}
