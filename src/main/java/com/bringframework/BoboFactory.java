package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.bringframework.exception.ExceptionErrorMessage.BOBO_INSTANTIATION_ERROR;

public class BoboFactory {
    @Getter()
    private final List<BoboConfigurator> boboConfigurators;
    private final BoboRegistry registry;

    public BoboFactory(BoboRegistry registry) {
        this.registry = registry;
        this.boboConfigurators = BoboConfiguratorScanner.scan("com.bringframework.configurator");
    }

    public BoboFactory(BoboRegistry registry, String... configuratorsPackages) {
        this.registry = registry;
        if (configuratorsPackages == null) {
            this.boboConfigurators = BoboConfiguratorScanner.scan("com.bringframework.configurator");
        } else {
            String[] packages = Arrays.copyOf(configuratorsPackages, configuratorsPackages.length + 1);
            packages[configuratorsPackages.length] = "com.bringframework.configurator";
            this.boboConfigurators = BoboConfiguratorScanner.scan(packages);
        }
    }

    public Object createBobo(BoboDefinition definition) {
        try {
            Object newBobo = instantiate(definition);

            configure(newBobo);

            invokeInit(definition, newBobo);

            return newBobo;
        } catch (Exception e) {
            throw new BoboException(String.format(BOBO_INSTANTIATION_ERROR, definition.getBoboName()), e);
        }
    }

    public void addBoboConfigurator(BoboConfigurator boboConfigurator) {
        Objects.requireNonNull(boboConfigurator, "BoboConfigurator must not be null");
        this.boboConfigurators.add(boboConfigurator);
    }

    private <T> T instantiate(BoboDefinition definition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (T) definition.getBoboClass().getDeclaredConstructor().newInstance();
    }

    private <T> void configure(T bobo) {
        boboConfigurators.forEach(boboConfigurator -> boboConfigurator.configure(bobo, registry));
    }

    private <T> void invokeInit(BoboDefinition definition, T bobo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (definition.getInitMethodName() != null) {
            Method initMethod = definition.getBoboClass().getMethod(definition.getInitMethodName());
            initMethod.invoke(bobo);
        }
    }
}
