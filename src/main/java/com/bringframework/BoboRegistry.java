package com.bringframework;

import com.bringframework.annotation.Item;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.NoSuchBoboDefinitionException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bringframework.exception.ExceptionErrorMessage.AMBIGUOUS_BOBO_ERROR;
import static com.bringframework.exception.ExceptionErrorMessage.NO_SUCH_BOBO_DEFINIITON_ERROR;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class BoboRegistry {

    private final Map<BoboDefinition, Object> registry;
    private final BoboFactory factory;
    private final ItemAnnotationBoboDefinitionScanner definitionScanner;

    public BoboRegistry(String packageToScan) {
        factory = new BoboFactory(this, packageToScan);
        registry = new ConcurrentHashMap<>();
        definitionScanner = new ItemAnnotationBoboDefinitionScanner(packageToScan);
        definitionScanner.scan().forEach(this::registerBobo);
        registry.values().forEach(factory::configure);
    }

    public <T> T getBobo(Class<T> type) {
        List<BoboDefinition> candidates = findCandidates(type);
        if (candidates.size() == 0) {
            throw new NoSuchBoboDefinitionException(
                    String.format(NO_SUCH_BOBO_DEFINIITON_ERROR, type.getSimpleName()));
        }
        if (candidates.size() > 1) {
            throw new AmbiguousBoboDefinitionException(String.format(
                    AMBIGUOUS_BOBO_ERROR, type.getCanonicalName(), candidates.size(),
                    candidates.stream().map(BoboDefinition::getBoboName).collect(joining(", ")))
            );
        }

        BoboDefinition boboDefinition = candidates.get(0);
        Object singletonBobo = registry.get(boboDefinition);
        return type.cast(singletonBobo);
    }

    public <T> T getBobo(String boboName, Class<T> type) {
        return type.cast(getBobo(boboName));
    }

    public Object getBobo(String boboName) {
        BoboDefinition boboDefinition = registry.keySet().stream()
                .filter(definition -> definition.getBoboName().equals(boboName))
                .findFirst()
                .orElseThrow(() -> new NoSuchBoboDefinitionException(String.format(NO_SUCH_BOBO_DEFINIITON_ERROR, boboName)));

        return registry.get(boboDefinition);
    }

    public boolean containsBobo(String boboName) {
        return registry.keySet().stream().anyMatch(definition -> definition.getBoboName().equals(boboName));
    }

    private void registerBobo(BoboDefinition boboDefinition) {
        if (boboDefinition.getBoboClass().isAnnotationPresent(Item.class)) {
            registry.put(boboDefinition, factory.createBobo(boboDefinition));
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
}
