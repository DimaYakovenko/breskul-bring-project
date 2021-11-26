package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bringframework.exception.ExceptionErrorMessage.AMBIGUOUS_BOBO_ERROR;
import static com.bringframework.exception.ExceptionErrorMessage.NO_SUCH_BOBO_DEFINIITON_ERROR;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
public class BoboRegistry {

    private final static Object EMPTY = new Object();
    private final Map<BoboDefinition, Object> registry = new ConcurrentHashMap<>();
    private final BoboFactory factory;

    public BoboRegistry() {
        factory = new BoboFactory(this);
    }

    public BoboRegistry(String... basePackages) {
        factory = new BoboFactory(this, basePackages);
        ItemAnnotationBoboDefinitionScanner.scan(basePackages)
                .forEach(definition -> registry.put(definition, factory.createBobo(definition)));
    }

    public BoboRegistry(Class<?>... itemClasses) {
        this();
        register(itemClasses);
    }

    public <T> T getBobo(Class<T> type) {
        List<BoboDefinition> candidates = findCandidates(type);
        int sizeOfCandidates = candidates.size();
        if (sizeOfCandidates == 0) {
            log.debug("There is no candidates for such BoboDefinition");
            throw new NoSuchBoboDefinitionException(
                    String.format(NO_SUCH_BOBO_DEFINIITON_ERROR, type.getSimpleName()));
        }
        if (sizeOfCandidates > 1) {
            log.error("Expected single matching bobo, but was {}", sizeOfCandidates);
            throw new AmbiguousBoboDefinitionException(String.format(
                    AMBIGUOUS_BOBO_ERROR, type.getCanonicalName(), sizeOfCandidates,
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
        BoboDefinition definitionByName = registry.keySet().stream()
                .filter(definition -> definition.getBoboName().equals(boboName))
                .findFirst()
                .orElseThrow(() -> new NoSuchBoboDefinitionException(String.format(NO_SUCH_BOBO_DEFINIITON_ERROR, boboName)));

        return getOrCreateBobo(definitionByName);
    }

    public void register(Class<?>... itemsClasses) {
        for (Class<?> itemClass : itemsClasses) {
            registerBoboDefinition(ItemAnnotationBoboDefinitionScanner.buildDefinition(itemClass));
        }
    }

    public void scan(String... basePackages) {
        ItemAnnotationBoboDefinitionScanner.scan(basePackages).forEach(definition -> registry.put(definition, EMPTY));
    }

    public void addBoboConfigurator(BoboConfigurator boboConfigurator) {
        factory.addBoboConfigurator(boboConfigurator);
    }

    private void registerBoboDefinition(BoboDefinition definition) {
        registry.put(definition, factory.createBobo(definition));
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
        if (singletonBobo != EMPTY) {
            return singletonBobo;
        }

        Object newBobo = factory.createBobo(definition);
        registry.put(definition, newBobo);

        return newBobo;
    }

}
