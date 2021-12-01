package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static com.bringframework.configurator.BoboConfiguratorScanner.DEFAULT_PACKAGE;
import static com.bringframework.exception.ExceptionErrorMessage.BOBO_INSTANTIATION_ERROR;
import static java.util.Objects.requireNonNull;

@Slf4j
public class BoboFactory {
    private final List<BoboConfigurator> boboConfigurators;
    private final BoboRegistry registry;

    public BoboFactory(BoboRegistry registry) {
        this.registry = registry;
        boboConfigurators = BoboConfiguratorScanner.scan(DEFAULT_PACKAGE);
    }

    public BoboFactory(BoboRegistry registry, String... configuratorsPackages) {
        this.registry = registry;
        if (configuratorsPackages == null) {
            boboConfigurators = BoboConfiguratorScanner.scan(DEFAULT_PACKAGE);
        } else {
            String[] packages = Arrays.copyOf(configuratorsPackages, configuratorsPackages.length + 1);
            packages[configuratorsPackages.length] = DEFAULT_PACKAGE;
            boboConfigurators = BoboConfiguratorScanner.scan(packages);
        }
    }

    public Object createBobo(BoboDefinition definition) {
        requireNonNull(definition, "BoboDefinition must not be null");
        String boboName = definition.getBoboName();
        try {
            log.debug("Starting create new bobo from definition {}", boboName);
            Object newBobo = instantiate(definition);

            configure(newBobo);

            invokeInit(definition, newBobo);

            return newBobo;
        } catch (BoboException boboException) {
            log.error("Can`t create new bobo {} from definition {}", boboName, definition);
            throw boboException;
        } catch (Exception e) {
            log.error("Can`t create new bobo {} from definition {}", boboName, definition);
            throw new BoboException(String.format(BOBO_INSTANTIATION_ERROR, definition.getBoboName()), e);
        }
    }

    public void addBoboConfigurator(BoboConfigurator boboConfigurator) {
        requireNonNull(boboConfigurator, "BoboConfigurator must not be null");
        boboConfigurators.add(boboConfigurator);
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
