package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.configurator.BoboProxyConfiguratorScanner;
import com.bringframework.configurator.proxyconfigurator.ProxyConfigurator;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.bringframework.configurator.BoboConfiguratorScanner.DEFAULT_PACKAGE;
import static com.bringframework.exception.ExceptionErrorMessage.BOBO_INSTANTIATION_EXCEPTION;
import static com.bringframework.exception.ExceptionErrorMessage.CIRCULAR_DEPENDENCY_EXCEPTION;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Class responsible for the creating and setting up new Bobo instances
 *
 * @author Andrii Bobrov
 * @author Mykhailo Pysarenko
 * @author Dmytro Yakovenko
 * @author Yuliia Smerechynska
 * @since 3 november 2021
 */
@Slf4j
public class BoboFactory {
    private final List<BoboConfigurator> boboConfigurators;
    private final List<ProxyConfigurator> proxyConfigurators;
    private final BoboRegistry registry;
    private final Map<BoboDefinition, Object> itemsInCreation = new ConcurrentHashMap<>();
    private final Set<BoboDefinition> constructorItemsInCreation = new HashSet<>();

    public BoboFactory(BoboRegistry registry) {
        this.registry = registry;
        boboConfigurators = BoboConfiguratorScanner.scan(DEFAULT_PACKAGE);
        proxyConfigurators = BoboProxyConfiguratorScanner.scan(DEFAULT_PACKAGE);
    }

    public BoboFactory(BoboRegistry registry, String... configuratorsPackages) {
        this.registry = registry;
        if (configuratorsPackages == null) {
            boboConfigurators = BoboConfiguratorScanner.scan(DEFAULT_PACKAGE);
            proxyConfigurators = BoboProxyConfiguratorScanner.scan(DEFAULT_PACKAGE);
        } else {
            String[] packages = Arrays.copyOf(configuratorsPackages, configuratorsPackages.length + 1);
            packages[configuratorsPackages.length] = DEFAULT_PACKAGE;
            boboConfigurators = BoboConfiguratorScanner.scan(packages);
            proxyConfigurators = BoboProxyConfiguratorScanner.scan(packages);
        }
    }

    public Object createBobo(BoboDefinition definition) {
        requireNonNull(definition, "BoboDefinition must not be null");
        String boboName = definition.getBoboName();
        if (itemsInCreation.containsKey(definition)) {
            log.debug("Found bobo that is currently in creation state, returning cached bobo {}", boboName);
            return itemsInCreation.get(definition);
        }
        try {
            log.debug("Starting create new bobo from definition {}", boboName);
            Object newBobo;
            if (definition.getConfigurationBoboName() != null) {
                newBobo = createBoboByConfigMethod(definition);
            } else {
                newBobo = instantiate(definition);

                configure(newBobo);
            }

            invokeInit(definition, newBobo);

            newBobo = wrapWithProxyIfNeeded(newBobo, definition.getBoboClass());
            itemsInCreation.remove(definition);
            return newBobo;
        } catch (BoboException boboException) {
            log.error("Can`t create new bobo {} from definition {}", boboName, definition);
            throw boboException;
        } catch (Exception e) {
            log.error("Can`t create new bobo {} from definition {}", boboName, definition);
            throw new BoboException(String.format(BOBO_INSTANTIATION_EXCEPTION, definition.getBoboName()), e);
        }
    }

    public void addBoboConfigurator(BoboConfigurator boboConfigurator) {
        requireNonNull(boboConfigurator, "BoboConfigurator must not be null");
        boboConfigurators.add(boboConfigurator);
    }

    private Object createBoboByConfigMethod(BoboDefinition definition) throws Exception {
        checkCircularDependency(definition);
        Object config = registry.getBobo(definition.getConfigurationBoboName());
        Method configurationMethod = definition.getConfigurationMethod();
        if (configurationMethod.getParameterCount() == 0) {
            return configurationMethod.invoke(config);
        }
        constructorItemsInCreation.add(definition);
        Object[] resolvedArgs = resolveBoboParameters(configurationMethod.getParameterTypes());
        var result = configurationMethod.invoke(config, resolvedArgs);
        constructorItemsInCreation.remove(definition);
        return result;
    }

    private <T> T instantiate(BoboDefinition definition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        checkCircularDependency(definition);
        Constructor<?> constructor = definition.getConstructor();
        constructor.setAccessible(true);
        if (constructor.getParameterCount() == 0) {
            var newBobo = (T) constructor.newInstance();
            itemsInCreation.put(definition, newBobo);
            return newBobo;
        }
        constructorItemsInCreation.add(definition);
        Object[] resolvedArgs = resolveBoboParameters(constructor.getParameterTypes());
        T result = (T) constructor.newInstance(resolvedArgs);
        constructorItemsInCreation.remove(definition);
        return result;
    }

    private Object[] resolveBoboParameters(Class<?>[] types) {
        Object[] resultArgs = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            resultArgs[i] = registry.getBobo(types[i]);
        }
        return resultArgs;
    }

    private <T> void configure(T bobo) {
        boboConfigurators.forEach(boboConfigurator -> boboConfigurator.configure(bobo, registry));
    }

    private <T> void invokeInit(BoboDefinition definition, T bobo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (definition.getInitMethodName() != null) {
            Method initMethod = definition.getBoboClass().getDeclaredMethod(definition.getInitMethodName());
            initMethod.invoke(bobo);
        }
    }

    private Object wrapWithProxyIfNeeded(Object newBobo, Class<?> boboClass) {
        for (ProxyConfigurator proxyConfigurator : proxyConfigurators) {
            newBobo = proxyConfigurator.replaceWithProxyIfNeeded(newBobo, boboClass, registry);
        }
        return newBobo;
    }

    private void checkCircularDependency(BoboDefinition definition) {
        if (constructorItemsInCreation.contains(definition)) {
            List<String> circularClassNames = constructorItemsInCreation.stream()
                    .map(df -> df.getBoboClass().getSimpleName()).collect(toList());
            log.error("Circular dependencies between classes: {}", circularClassNames);
            constructorItemsInCreation.clear();
            throw new BoboException(String.format(CIRCULAR_DEPENDENCY_EXCEPTION, circularClassNames));
        }
    }
}
