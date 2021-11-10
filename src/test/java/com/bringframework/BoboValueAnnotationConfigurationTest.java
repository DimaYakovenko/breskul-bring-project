package com.bringframework;

import com.bringframework.annotation.BoboValue;
import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import demonstration.project.service.MyService;
import demonstration.project.service.impl.MyServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoboValueAnnotationConfigurationTest {

    private final String TEST_RESOURCES_DIRECTORY_PATH = "src/main/resources";
    private final String TEST_PROPERTIES_FILE_PATH = "src/main/resources/application.properties";
    private final List<String> TEST_PROPERTY_DATA = Arrays.asList("some.string.value=value", "some.int.value=5", "defaultValue=DefaultValue");
    private final String TEST_DEMONSTRATION_PACKAGE_PATH = "demonstration.project";

    @Test
    public void findAllFieldsWithAnnotationBoboValue() {
        createTestResources();
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        List<Field> fields = Stream.of(myService.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(BoboValue.class))
                .collect(Collectors.toList());

        assertEquals(3, fields.size());
        deleteTestResources();
    }

    @Test
    public void checkStringFieldValueWithAnnotationBoboValue() {
        createTestResources();
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        assertEquals("value", myService.getStringValue());
        assertEquals(String.class, myService.getStringValue().getClass());
        deleteTestResources();
    }

    @Test
    public void checkPrimitiveFieldValueWithAnnotationBoboValue() {
        createTestResources();
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        assertEquals(5, myService.getIntValue());
        deleteTestResources();
    }

    @Test
    public void checkFieldValueWithAnnotationBoboValueWithoutParam() {
        createTestResources();
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);
        assertEquals("DefaultValue", myService.getDefaultValue());
        deleteTestResources();
    }

    private void createTestResources() {
        try {
            Files.createDirectories(Paths.get(TEST_RESOURCES_DIRECTORY_PATH));
            Path file = Paths.get(TEST_PROPERTIES_FILE_PATH);
            Files.write(file, TEST_PROPERTY_DATA, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Can't create properties file");
            e.printStackTrace();
        }
    }

    private void deleteTestResources() {
        try {
            Files.delete(Paths.get(TEST_PROPERTIES_FILE_PATH));
            Files.delete(Paths.get(TEST_RESOURCES_DIRECTORY_PATH));
        } catch (IOException e) {
            System.out.println("Can't delete properties file");
            e.printStackTrace();
        }
    }


    //Here I try to create getters for a class by reflection. Maybe I will delete it soon.
    private Class<?> addGettersToClass(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            createGetter(fieldName, fieldType, builder);
            System.out.println(builder.toString());
        }
        return clazz;
    }

    private void createGetter(String fieldName, String fieldType, StringBuilder getter) {
        getter.append("public ")
                .append(fieldType)
                .append(fieldType.equals("boolean") ? " is" : " get")
                .append(getFieldName(fieldName))
                .append("(){")
                .append("\n\treturn ")
                .append(fieldName)
                .append(";")
                .append("\n" + "}" + "\n");
    }

    private String getFieldName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
