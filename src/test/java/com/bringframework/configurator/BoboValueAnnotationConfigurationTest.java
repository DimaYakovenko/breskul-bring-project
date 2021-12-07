package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.exception.BoboException;
import broken_items.InvalidPropertyClassCastService;
import items.service.impl.FakeUserServiceImpl;
import broken_items.InvalidPropertyNumberFormatService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BoboValueAnnotationConfigurationTest {

    private final String INCORRECT_PATH_TO_PROPERTIES_FILE = "bring/breskul-bring-project/target/test-classes";
    private final String NOT_FOUND_FILE_MSG = "Can't find properties file in resources with name application.properties";
    private final String CLASS_CAST_EXCEPTION_MSG = "Can't cast value \"not_valid_value\" to FakeUser class";
    private final String BOBO_EXCEPTION_MSG = "Can't set value \"not_valid_value\" from properties to field \"fakeUser\" in class broken_items.InvalidPropertyClassCastService";

    @Test
    @SneakyThrows
    void whenApplicationPropertiesFileExistsAndHasProperties_thenFillPropertyMaps() {
        BoboConfigurator boboConfigurator = new BoboValueAnnotationConfiguration();
        Field configMapField = boboConfigurator.getClass().getDeclaredField("propertiesMap");
        configMapField.setAccessible(true);
        Map<String, String> configMap = (Map<String, String>) configMapField.get(boboConfigurator);
        assertEquals(5, configMap.size());
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

    @Test
    void checkExceptionTypes_whenCanNotSetValueToField() {
        BoboRegistry mockRegistry = mock(BoboRegistry.class);
        BoboValueAnnotationConfiguration configurator = new BoboValueAnnotationConfiguration();

        InvalidPropertyNumberFormatService invalidPropertyNumberFormatService = new InvalidPropertyNumberFormatService();
        InvalidPropertyClassCastService invalidPropertyClassCastService = new InvalidPropertyClassCastService();

        Exception numberFormatException = assertThrows(BoboException.class,
                () -> configurator.configure(invalidPropertyNumberFormatService, mockRegistry));

        Exception classCastException = assertThrows(BoboException.class,
                () -> configurator.configure(invalidPropertyClassCastService, mockRegistry));

        assertSame(NumberFormatException.class, numberFormatException.getCause().getClass());
        assertSame(ClassCastException.class, classCastException.getCause().getClass());

    }

    @Test
    void checkExceptionMSG_whenCanNotSetValueToField() {
        BoboRegistry mockRegistry = mock(BoboRegistry.class);
        BoboValueAnnotationConfiguration configurator = new BoboValueAnnotationConfiguration();

        InvalidPropertyClassCastService invalidPropertyClassCastService = new InvalidPropertyClassCastService();
        Exception classCastException = assertThrows(BoboException.class,
                () -> configurator.configure(invalidPropertyClassCastService, mockRegistry));

        assertEquals(BOBO_EXCEPTION_MSG, classCastException.getMessage());
        assertEquals(CLASS_CAST_EXCEPTION_MSG, classCastException.getCause().getMessage());
    }
}
