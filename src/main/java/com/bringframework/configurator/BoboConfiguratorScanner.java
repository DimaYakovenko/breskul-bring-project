package com.bringframework.configurator;

import com.bringframework.exception.BoboException;
import org.reflections.Reflections;

import java.util.List;
import java.util.stream.Collectors;

public class BoboConfiguratorScanner {
    private final Reflections scanner;

    public BoboConfiguratorScanner(String packageToScan) {
        scanner = new Reflections("com.bringframework", packageToScan);
    }

    public List<BoboConfigurator> scan() {
        return scanner.getSubTypesOf(BoboConfigurator.class)
                .stream()
                .map(this::createInstance)
                .collect(Collectors.toList());
    }

    private BoboConfigurator createInstance(Class<? extends BoboConfigurator> configuratorClass) {
        try {
            return configuratorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BoboException("Can't create an instance of " + configuratorClass);
        }
    }

}
