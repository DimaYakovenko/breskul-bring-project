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

    private final String TEST_RESOURCES_DIRECTORY_PATH = "src/main/resources";
    private final String TEST_PROPERTIES_FILE_PATH = "src/main/resources/application.properties";
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

    @SneakyThrows
    @Test
    public void checkStringFieldValueWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        Field stringField = myService.getClass().getDeclaredField("stringValue");
        stringField.setAccessible(true);
        assertEquals("value", stringField.get(myService));
        assertEquals(String.class, stringField.get(myService).getClass());
    }

    @SneakyThrows
    @Test
    public void checkPrimitiveFieldValueWithAnnotationBoboValue() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        Field intField = myService.getClass().getDeclaredField("intValue");
        intField.setAccessible(true);
        assertEquals(5, intField.get(myService));
    }

    @SneakyThrows
    @Test
    public void checkFieldValueWithAnnotationBoboValueWithoutParam() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner(TEST_DEMONSTRATION_PACKAGE_PATH).scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyServiceImpl myService = (MyServiceImpl) boboFactory.getBobo(MyService.class);

        Field defaultField = myService.getClass().getDeclaredField("defaultValue");
        defaultField.setAccessible(true);
        assertEquals("DefaultValue", defaultField.get(myService));
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
