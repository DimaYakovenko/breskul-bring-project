package com.bringframework;

import com.bringframework.annotation.Item;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.NoSuchBoboDefinitionException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class BoboRegistry {

    private final static Object EMPTY = new Object();
    private final Map<BoboDefinition, Object> registry;
    private final BoboFactory factory;
    private final ItemAnnotationBoboDefinitionScanner definitionScanner;

    public BoboRegistry(String packageToScan) {
        definitionScanner = new ItemAnnotationBoboDefinitionScanner(packageToScan);
        registry = new ConcurrentHashMap<>();
        definitionScanner.scan().forEach(definition -> registry.put(definition, EMPTY));
        factory = new BoboFactory(this, packageToScan);
    }

    public <T> T getBobo(Class<T> type) {
        List<BoboDefinition> candidates = findCandidates(type);
        if (candidates.size() == 0) {
            throw new NoSuchBoboDefinitionException("No such bobo definition for type '" + type.getSimpleName() + "'");
        }
        if (candidates.size() > 1) {
            throw new AmbiguousBoboDefinitionException(String.format(
                    "No qualifying bobo of type '%s' available: expected single matching bobo but found %d: %s",
                    type.getCanonicalName(),
                    candidates.size(),
                    candidates.stream().map(BoboDefinition::getBoboName).collect(joining(", ")))
            );
        }

        BoboDefinition definition = candidates.get(0);
        return getOrCreateBean(definition, type);
    }

    public <T> T getBobo(String boboName, Class<T> type) {
        BoboDefinition definitionByName = registry.keySet().stream()
                .filter(definition -> definition.getBoboName().equals(boboName))
                .findFirst()
                .orElseThrow(() -> new NoSuchBoboDefinitionException("No such bobo definition: '" + boboName + "'"));

        return getOrCreateBean(definitionByName, type);
    }

    public void registerBoboDefinition(BoboDefinition definition) {
        registry.put(definition, EMPTY);
    }

    public void register(Class<?>... itemsClasses) {
        for (Class<?> itemClass : itemsClasses) {
            BoboDefinition boboDefinition = definitionScanner.buildDefinition(itemClass);
            registry.put(boboDefinition, EMPTY);
        }
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

    private <T> T getOrCreateBean(BoboDefinition definition, Class<T> type) {
        Object singletonBobo = registry.get(definition);
        if (singletonBobo != EMPTY) {
            return type.cast(singletonBobo);
        }

        T newBobo = factory.createBobo(definition);
        if (definition.getBoboClass().isAnnotationPresent(Item.class)) {
            registry.put(definition, newBobo);
        }
        return newBobo;
    }

}
