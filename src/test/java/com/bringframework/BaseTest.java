package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTest {

    protected static final String PACKAGE_TO_SCAN = "items";

    @SneakyThrows
    protected <T> void registerBobo(BoboRegistry registry, BoboDefinition boboDefinition, T bobo) {
        Field registryMapField = registry.getClass().getDeclaredField("registry");
        registryMapField.setAccessible(true);
        ConcurrentHashMap<BoboDefinition, Object> registryMap = (ConcurrentHashMap<BoboDefinition, Object>) registryMapField.get(registry);
        registryMap.put(boboDefinition, bobo);
    }
}
