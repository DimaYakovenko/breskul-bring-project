package com.bringframework.definition;

import com.bringframework.annotation.Configuration;
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
        Set<Class<?>> configs = new Reflections(basePackages, Scanners.TypesAnnotated)
                .getTypesAnnotatedWith(Configuration.class);
        items.addAll(configs);

        return items.stream()
                .map(BoboDefinitionUtil::buildDefinition)
                .collect(toList());
    }
}
