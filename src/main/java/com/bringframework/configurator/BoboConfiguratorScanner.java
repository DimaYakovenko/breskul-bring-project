package com.bringframework.configurator;

import lombok.SneakyThrows;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class BoboConfiguratorScanner {
    private final Reflections scanner;

    public BoboConfiguratorScanner(String packageToScan) {
        scanner = new Reflections("com.bringframework", packageToScan);
    }

    @SneakyThrows
    public List<BoboConfigurator> scan() {
        List<BoboConfigurator> configurators = new ArrayList<>();
        for (Class<? extends BoboConfigurator> aClass : scanner.getSubTypesOf(BoboConfigurator.class)) {
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }
        return configurators;
    }
}
