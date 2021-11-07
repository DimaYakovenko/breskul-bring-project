package com.bringframework.definition;

import com.bringframework.annotation.Item;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;

import static java.beans.Introspector.decapitalize;
import static java.util.stream.Collectors.toList;

public class ItemAnnotationBoboDefinitionScanner {

    public static List<BoboDefinition> scan(String... basePackages) {
        return new Reflections(basePackages, Scanners.TypesAnnotated)
                .getTypesAnnotatedWith(Item.class)
                .stream()
                .map(ItemAnnotationBoboDefinitionScanner::buildDefinition)
                .collect(toList());
    }

    public static BoboDefinition buildDefinition(Class<?> type) {
        return BoboDefinition.builder()
                .boboName(generateBoboName(type))
                .boboClass(type)
                .build();
    }

    private static String generateBoboName(Class<?> type) {
        return decapitalize(type.getSimpleName());
    }

}
