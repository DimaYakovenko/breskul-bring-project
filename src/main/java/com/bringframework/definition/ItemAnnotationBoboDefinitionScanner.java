package com.bringframework.definition;

import com.bringframework.annotation.Item;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.beans.Introspector.decapitalize;

public class ItemAnnotationBoboDefinitionScanner {

    private final Reflections scanner;

    public ItemAnnotationBoboDefinitionScanner(String packageToScan) {
        scanner = new Reflections(packageToScan);
    }

    public List<BoboDefinition> scan() {
        Set<Class<?>> itemClasses = scanner.getTypesAnnotatedWith(Item.class);
        return itemClasses.stream()
                .map(type -> BoboDefinition.builder().boboName(generateBoboName(type)).boboClass(type).build())
                .collect(Collectors.toList());
    }

    private String generateBoboName(Class<?> type) {
        return decapitalize(type.getSimpleName());
    }

}
