package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.configurator.InjectAnnotationBoboConfigurator;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.BoboException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import demonstration.project.dao.impl.MyDaoImpl;
import demonstration.project.service.MyService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BoboFactoryTest {

    private static final String TEST_RESOURCES_DIRECTORY_PATH = "src/main/resources";
    private static final String TEST_PROPERTIES_FILE_PATH = "src/main/resources/application.properties";
    private static final List<String> TEST_PROPERTY_DATA = Arrays.asList("some.string.value=value", "some.int.value=5", "defaultValue=DefaultValue");

    @BeforeAll
    private static void createTestResources() {
        try {
            Files.createDirectories(Paths.get(TEST_RESOURCES_DIRECTORY_PATH));
            Path file = Paths.get(TEST_PROPERTIES_FILE_PATH);
            Files.write(file, TEST_PROPERTY_DATA, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Can't create properties file");
            e.printStackTrace();
        }
    }

    @AfterAll
    private static void deleteTestResources() {
        try {
            Files.delete(Paths.get(TEST_PROPERTIES_FILE_PATH));
            Files.delete(Paths.get(TEST_RESOURCES_DIRECTORY_PATH));
        } catch (IOException e) {
            System.out.println("Can't delete properties file");
            e.printStackTrace();
        }
    }

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
        BoboFactory boboFactory = new BoboFactory(definitions, List.of(new InjectAnnotationBoboConfigurator()));

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
        List<BoboConfigurator> configurators = List.of(new InjectAnnotationBoboConfigurator());
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
}
