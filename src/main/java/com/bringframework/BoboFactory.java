package com.bringframework;

import com.bringframework.annotation.Item;
import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.BoboException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import com.bringframework.exception.AmbiguousBoboDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class BoboFactory {
    private final static Object EMPTY = new Object();
    private final Map<BoboDefinition, Object> registry;
    private final List<BoboConfigurator> configurators;

    public BoboFactory(List<BoboDefinition> boboDefinitionsList, List<BoboConfigurator> configurators) {
        this.registry = new ConcurrentHashMap<>();
        boboDefinitionsList.forEach(definition -> registry.put(definition, EMPTY));
        this.configurators = configurators;
    }

    public <T> T getBobo(Class<T> type) {
        List<BoboDefinition> candidates = findCandidates(type);
        if (candidates.size() == 0) {
            throw new NoSuchBoboDefinitionException("No such bobo definition for type '" + type.getSimpleName() + "'");
        }
        if (candidates.size() > 1) {
            throw new AmbiguousBoboDefinitionException(
                    String.format(
                            "No qualifying bobo of type '%s' available: expected single matching bobo but found %d: %s",
                            type.getCanonicalName(),
                            candidates.size(),
                            candidates.stream().map(BoboDefinition::getBoboName).collect(joining(", "))));
        }

        BoboDefinition definition = candidates.get(0);
        Object singletonBobo = registry.get(definition);
        if (singletonBobo != EMPTY) {
            return type.cast(singletonBobo);
        }

        T newBobo = createBobo(definition);
        if (definition.getBoboClass().isAnnotationPresent(Item.class)) {
            registry.put(definition, newBobo);
        }
        return newBobo;
    }

    //TODO check method add tests
    public Object getBobo(String boboName) {
        BoboDefinition definitionByName = registry.keySet().stream()
                .filter(o -> o.getBoboName().equals(boboName))
                .findFirst().orElseThrow(() -> new NoSuchBoboDefinitionException("No such bobo definition: '" + boboName +"'"));

        Object singletonBobo = registry.get(definitionByName);
        if (singletonBobo != EMPTY) {
            return singletonBobo;
        }
        Object newBobo = createBobo(definitionByName);
        if (definitionByName.getBoboClass().isAnnotationPresent(Item.class)) {
            registry.put(definitionByName, newBobo);
        }
        return newBobo;
    }

    private <T> List<BoboDefinition> findCandidates(Class<T> type) {
        return registry.keySet().stream()
                .filter(definition -> definition.getBoboClass() == type || isTypeOfInterface(definition.getBoboClass(), type))
                .collect(toList());
    }

    private <T> boolean isTypeOfInterface(Class<?> boboClass, Class<T> type) {
        if (!type.isInterface()) {
            return false;
        }
        for (Class<?> classInterface : boboClass.getInterfaces()) {
            if (classInterface == type) {
                return true;
            }
        }
        return false;
    }

    private <T> T createBobo(BoboDefinition definition) {
        try {
            T newBobo = instantiate(definition);

            configure(newBobo);

            invokeInit(definition, newBobo);

            return newBobo;

        } catch (Exception e) {
            throw new BoboException("Cannot instantiate bobo: " + definition.getBoboName(), e);
        }
    }

    private <T> T instantiate(BoboDefinition definition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (T) definition.getBoboClass().getDeclaredConstructor().newInstance();
    }

    private <T> void configure(T bobo) {
        configurators.forEach(boboConfigurator -> boboConfigurator.configure(bobo, this));
    }

    private <T> void invokeInit(BoboDefinition definition, T bobo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (definition.getInitMethodName() != null) {
            Method initMethod = definition.getBoboClass().getMethod(definition.getInitMethodName());
            initMethod.invoke(bobo);
        }
    }

}
