package com.bringframework;

import com.bringframework.annotation.BoboValue;
import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.configurator.InjectAnnotationBoboConfigurator;
import com.bringframework.configurator.BoboValueAnnotationConfiguration;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.BoboException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import demonstration.project.dao.impl.MyDaoImpl;
import demonstration.project.service.MyService;
import demonstration.project.service.impl.MyServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BoboFactoryTest {

    @Test
    public void getBoboByType_whenGetBoboByType_returnValidBobo() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyService myService = boboFactory.getBobo(MyService.class);
        assertNotNull(myService);
        assertEquals("It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02", myService.showMe());
    }

    @Test
    public void getBoboByType_whenGetTwoSingletonBobo_shouldReturnTheSameInstance() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyService bobo = boboFactory.getBobo(MyService.class);
        assertNotNull(bobo);
        MyService bobo2 = boboFactory.getBobo(MyService.class);
        assertNotNull(bobo2);
        assertEquals(bobo, bobo2);
    }

    @Test
    public void getBoboByType_whenBoboDefinitionNotFound_throwNoSuchBoboDefinitionException() {
        BoboFactory boboFactory = new BoboFactory(emptyList(), emptyList());
        Class<MyService> myBoboClass = MyService.class;

        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> {
            boboFactory.getBobo(myBoboClass);
        });

        assertEquals("No such bobo definition for type '" + myBoboClass.getSimpleName() + "'", exception.getMessage());
    }

    @Test
    public void getBoboByType_whenBoboDefinitionMoreThenOneFound_throwAmbiguousBoboDefinitionException() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        definitions.add(BoboDefinition.builder().boboName("dao").boboClass(MyDaoImpl.class).build());
        BoboFactory boboFactory = new BoboFactory(definitions, List.of(new InjectAnnotationBoboConfigurator(), new BoboValueAnnotationConfiguration()));

        AmbiguousBoboDefinitionException actualException = assertThrows(AmbiguousBoboDefinitionException.class, () -> {
            boboFactory.getBobo(MyDaoImpl.class);
        });
        assertEquals(
                "No qualifying bobo of type 'demonstration.project.dao.impl.MyDaoImpl' available: expected single matching bobo but found 2:",
                actualException.getMessage().substring(0, 123)
        );
        String boboNameList = actualException.getMessage().substring(124);
        assertTrue(boboNameList.contains("dao"));
        assertTrue(boboNameList.contains("myDaoImpl"));
    }

    @Test
    public void getBoboByType_whenBoboDefinitionMoreThanOneFound_throwBoboExceptionWithNestedAmbiguousBoboDefinitionException() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        definitions.add(BoboDefinition.builder().boboName("myDaoImpl1").boboClass(MyDaoImpl.class).build());
        List<BoboConfigurator> configurators = List.of(new InjectAnnotationBoboConfigurator(), new BoboValueAnnotationConfiguration());
        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        BoboException actualException = assertThrows(BoboException.class, () -> {
            boboFactory.getBobo(MyService.class);
        });

        assertEquals("Cannot instantiate bobo: myServiceImpl", actualException.getMessage());
        assertTrue(actualException.getCause() instanceof AmbiguousBoboDefinitionException);
        assertEquals(
                "No qualifying bobo of type 'demonstration.project.dao.MyDao' available: expected single matching bobo but found 2:",
                actualException.getCause().getMessage().substring(0, 114)
        );
        String boboNameList = actualException.getCause().getMessage().substring(114);
        assertTrue(boboNameList.contains("myDaoImpl"));
        assertTrue(boboNameList.contains("myDaoImpl1"));
    }

    @Test
    public void findAllFieldsWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        List<Field> fields = Stream.of(myService.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(BoboValue.class))
                .collect(Collectors.toList());

        fields.forEach(field -> System.out.println(field.getName()));
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
