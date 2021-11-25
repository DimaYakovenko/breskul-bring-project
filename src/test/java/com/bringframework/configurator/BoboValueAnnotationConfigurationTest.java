package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.exception.BoboException;
import items.service.impl.FakeUserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BoboValueAnnotationConfigurationTest {
    private final String INCORRECT_PATH_TO_PROPERTIES_FILE = "bring/breskul-bring-project/target/test-classes";
    private final String NOT_FOUND_FILE_MSG = "Can't find properties file in resources with name application.properties";

    @Test
    @SneakyThrows
    void whenApplicationPropertiesFileExistsAndHasProperties_thenFillPropertyMaps() {
        BoboConfigurator boboConfigurator = new BoboValueAnnotationConfiguration();
        Field configMapField = boboConfigurator.getClass().getDeclaredField("propertiesMap");
        configMapField.setAccessible(true);
        Map<String, String> configMap = (Map<String, String>) configMapField.get(boboConfigurator);
        assertEquals(3, configMap.size());
    }

    @Test
    void checkCastTypesFromPropertiesToFields() {
        BoboRegistry mockRegistry = mock(BoboRegistry.class);
        BoboValueAnnotationConfiguration configurator = new BoboValueAnnotationConfiguration();
        FakeUserServiceImpl fakeUserService = new FakeUserServiceImpl();
        configurator.configure(fakeUserService, mockRegistry);

        assertEquals(String.class, fakeUserService.getStringValue().getClass());
        assertEquals(Integer.class, ((Object) fakeUserService.getIntValue()).getClass() );
        assertEquals(String.class, fakeUserService.getDefaultValue().getClass());
    }

    @Test
    void checkFieldsValueFromProperties() {
        BoboRegistry mockRegistry = mock(BoboRegistry.class);
        BoboValueAnnotationConfiguration configurator = new BoboValueAnnotationConfiguration();
        FakeUserServiceImpl fakeUserService = new FakeUserServiceImpl();
        configurator.configure(fakeUserService, mockRegistry);

        assertEquals("value", fakeUserService.getStringValue());
        assertEquals(5, fakeUserService.getIntValue());
        assertEquals("DefaultValue", fakeUserService.getDefaultValue());
        assertNull(fakeUserService.getEmptyValue());
    }

    @Test
    @SneakyThrows
    void whenPathToFileProperties_isNotCorrect() {
        BoboConfigurator boboConfigurator = new BoboValueAnnotationConfiguration();
        Method readFileMethod = boboConfigurator.getClass().getDeclaredMethod("readPropertiesByPath", String.class);
        readFileMethod.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class,
                () -> readFileMethod.invoke(boboConfigurator, INCORRECT_PATH_TO_PROPERTIES_FILE));
        assertEquals(BoboException.class, exception.getCause().getClass());
        assertEquals(NOT_FOUND_FILE_MSG, exception.getCause().getMessage());
    }
}
