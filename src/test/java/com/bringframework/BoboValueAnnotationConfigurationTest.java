package com.bringframework;

import com.bringframework.annotation.BoboValue;
import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import demonstration.project.service.MyService;
import demonstration.project.service.impl.MyServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoboValueAnnotationConfigurationTest {

    private final String TEST_DEMONSTRATION_PACKAGE_PATH = "demonstration.project";

    @Test
    public void findAllFieldsWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        List<Field> fields = Stream.of(myService.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(BoboValue.class))
                .collect(Collectors.toList());

        assertEquals(3, fields.size());
    }

    @Test
    public void checkStringFieldValueWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        assertEquals("value", myService.getStringValue());
        assertEquals(String.class, myService.getStringValue().getClass());
    }

    @Test
    public void checkPrimitiveFieldValueWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        assertEquals(5, myService.getIntValue());
    }

    @Test
    public void checkFieldValueWithAnnotationBoboValueWithoutParam() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);
        assertEquals("DefaultValue", myService.getDefaultValue());
    }
}
