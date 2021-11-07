package com.bringframework.definition;

import demonstration.project.dao.impl.MyDaoImpl;
import demonstration.project.service.impl.MyServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemAnnotationBoboDefinitionScannerTest {

    @Test
    void scan_whenScanPackageWithOneItem_ReturnValidDefinitions() {
        List<BoboDefinition> actual = ItemAnnotationBoboDefinitionScanner.scan("demonstration.project.dao.impl");
        assertEquals(1, actual.size());
        assertEquals(
                BoboDefinition.builder().boboName("myDaoImpl").boboClass(MyDaoImpl.class).build(),
                actual.get(0)
        );
    }

    @Test
    void buildDefinition_whenCallBuildDefinition_returnValidBoboDefinition() {
        assertEquals(
                BoboDefinition.builder().boboName("myServiceImpl").boboClass(MyServiceImpl.class).build(),
                ItemAnnotationBoboDefinitionScanner.buildDefinition(MyServiceImpl.class)
        );
    }
}