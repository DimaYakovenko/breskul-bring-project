package com.bringframework.definition;

import com.bringframework.annotation.Item;
import com.bringframework.util.BoboDefinitionUtil;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ItemAnnotationBoboDefinitionScanner {

    public static List<BoboDefinition> scan(String... basePackages) {
        Set<Class<?>> items = new Reflections(basePackages, Scanners.TypesAnnotated).getTypesAnnotatedWith(Item.class);

        return items.stream()
                .map(BoboDefinitionUtil::buildDefinition)
                .collect(toList());
    }
}
