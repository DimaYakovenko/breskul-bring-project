package com.bringframework.configurator;

import com.bringframework.exception.BoboException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.stream.Collectors;

public class BoboConfiguratorScanner {

    public static List<BoboConfigurator> scan(String... packageToScan) {
        return new Reflections(packageToScan, Scanners.SubTypes)
                .getSubTypesOf(BoboConfigurator.class)
                .stream()
                .map(BoboConfiguratorScanner::createInstance)
                .collect(Collectors.toList());
    }

    private static BoboConfigurator createInstance(Class<? extends BoboConfigurator> configuratorClass) {
        try {
            return configuratorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BoboException("Can't create an instance of " + configuratorClass, e);
        }
    }

}
