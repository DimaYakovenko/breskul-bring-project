package com.bringframework;

import com.bringframework.annotation.BoboValue;
import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import demonstration.project.service.MyService;
import demonstration.project.service.impl.MyServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoboValueAnnotationConfigurationTest {

    @Test
    public void findAllFieldsWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        List<Field> fields = Stream.of(myService.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(BoboValue.class))
                .collect(Collectors.toList());

        assertEquals(3, fields.size());
    }

    @SneakyThrows
    @Test
    public void checkStringFieldValueWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        Field stringField = myService.getClass().getDeclaredField("stringValue");
        stringField.setAccessible(true);
        assertEquals("value", stringField.get(myService));
        assertEquals(String.class, stringField.get(myService).getClass() );
    }

    @SneakyThrows
    @Test
    public void checkPrimitiveFieldValueWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        Field intField = myService.getClass().getDeclaredField("intValue");
        intField.setAccessible(true);
        assertEquals(5, intField.get(myService));
    }

    @SneakyThrows
    @Test
    public void checkFieldValueWithAnnotationBoboValueWithoutParam() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        Field defaultField = myService.getClass().getDeclaredField("defaultValue");
        defaultField.setAccessible(true);
        assertEquals("DefaultValue", defaultField.get(myService));
    }

}