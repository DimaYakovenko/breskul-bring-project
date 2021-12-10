package com.bringframework.definition;

import com.bringframework.annotation.Item;
import com.bringframework.util.BoboDefinitionUtil;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Utility class for scanning packages to get types annotated
 * with {@link Item} annotations and parse their {@link BoboDefinition}
 *
 * @author Andrii Bobrov
 * @author Mykhailo Pysarenko
 * @since 8 december 2021
 */
@UtilityClass
public class ItemAnnotationBoboDefinitionScanner {

    /**
     * Scan packages and build {@link BoboDefinition} from classes annotated by {@link Item}
     *
     * @param basePackages packages to scan
     * @return List of {@link BoboDefinition}
     */
    public static List<BoboDefinition> scan(String... basePackages) {
        Set<Class<?>> items = new Reflections(basePackages, Scanners.TypesAnnotated).getTypesAnnotatedWith(Item.class);

        return items.stream()
                .map(BoboDefinitionUtil::buildDefinition)
                .collect(toList());
    }
}
