package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ConfigurationAnnotationBoboDefinitionScanner;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import com.bringframework.util.BoboDefinitionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.bringframework.exception.ExceptionErrorMessage.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * The root class for accessing a Bring object container.
 */
@Slf4j
public class BoboRegistry {

    private final Map<BoboDefinition, Object> registry = new ConcurrentHashMap<>();
    private final BoboFactory factory;

    public BoboRegistry() {
        factory = new BoboFactory(this);
    }

    public BoboRegistry(String... basePackages) {
        factory = new BoboFactory(this, basePackages);
        Consumer<BoboDefinition> fillRegistry = definition -> registry.put(definition, NullBobo.NULL);

        ItemAnnotationBoboDefinitionScanner.scan(basePackages).forEach(fillRegistry);
        ConfigurationAnnotationBoboDefinitionScanner.scan(basePackages).forEach(fillRegistry);

        refresh();
    }

    public void refresh() {
        registry.replaceAll((definition, old) -> NullBobo.NULL);
        for (Map.Entry<BoboDefinition, Object> entry : registry.entrySet()) {
            entry.setValue(factory.createBobo(entry.getKey()));
        }
    }

    public BoboRegistry(Class<?>... itemClasses) {
        this();
        register(itemClasses);
        refresh();
    }

    public <T> T getBobo(Class<T> type) {
        List<BoboDefinition> candidates = findCandidates(type);
        int sizeOfCandidates = candidates.size();
        if (sizeOfCandidates == 0) {
            log.debug("There is no candidates for such BoboDefinition");
            throw new NoSuchBoboDefinitionException(
                    String.format(NO_SUCH_BOBO_DEFINITION_BY_TYPE_EXCEPTION, type.getSimpleName()));
        }
        if (sizeOfCandidates > 1) {
            log.error("Expected single matching bobo, but was {}", sizeOfCandidates);
            throw new AmbiguousBoboDefinitionException(String.format(
                    AMBIGUOUS_BOBO_EXCEPTION, type.getCanonicalName(), sizeOfCandidates,
                    candidates.stream()
                            .map(BoboDefinition::getBoboName)
                            .collect(joining(", ")))
            );
        }

        BoboDefinition definition = candidates.get(0);
        return type.cast(getOrCreateBobo(definition));
    }

    public <T> T getBobo(String boboName, Class<T> type) {
        return type.cast(getBobo(boboName));
    }

    public Object getBobo(String boboName) {
        return getOrCreateBobo(getBoboDefinition(boboName));
    }

    public void register(Class<?>... itemsClasses) {
        for (Class<?> itemClass : itemsClasses) {
            registerBoboDefinition(BoboDefinitionUtil.buildDefinition(itemClass));
        }
    }

    public void scan(String... basePackages) {
        ItemAnnotationBoboDefinitionScanner.scan(basePackages).forEach(definition -> registry.put(definition, NullBobo.NULL));
    }

    public void addBoboConfigurator(BoboConfigurator boboConfigurator) {
        factory.addBoboConfigurator(boboConfigurator);
    }

    public boolean contains(String boboName) {
        return registry.entrySet()
                .stream()
                .anyMatch(entry -> entry.getKey().getBoboName().equals(boboName) && entry.getValue() != NullBobo.NULL);
    }

    public boolean containsDefinition(String boboName) {
        return registry.entrySet().stream()
                .anyMatch(entry -> entry.getKey().getBoboName().equals(boboName));
    }

    public void putBobo(BoboDefinition definition, Object bobo) {
        registry.put(definition, bobo);
    }

    public BoboDefinition getBoboDefinition(String boboName) {
        return registry.keySet().stream()
                .filter(definition -> definition.getBoboName().equals(boboName))
                .findFirst()
                .orElseThrow(() -> new NoSuchBoboDefinitionException(String.format(NO_SUCH_BOBO_DEFINITION_BY_NAME_EXCEPTION, boboName)));
    }

    private void registerBoboDefinition(BoboDefinition definition) {
        registry.put(definition, NullBobo.NULL);
    }

    private List<BoboDefinition> findCandidates(Class<?> type) {
        return registry.keySet().stream()
                .filter(definition -> definition.getBoboClass() == type || isTypeOfInterface(definition.getBoboClass(), type))
                .collect(toList());
    }

    private boolean isTypeOfInterface(Class<?> boboClass, Class<?> type) {
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

    private Object getOrCreateBobo(BoboDefinition definition) {
        Object singletonBobo = registry.get(definition);
        if (singletonBobo != NullBobo.NULL) {
            return singletonBobo;
        }

        Object newBobo = factory.createBobo(definition);
        registry.put(definition, newBobo);

        return newBobo;
    }

    private enum NullBobo {
        NULL;

        @Override
        public String toString() {
            return "null";
        }
    }

}
