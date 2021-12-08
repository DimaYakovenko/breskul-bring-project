package com.bringframework.definition;

import com.bringframework.util.BoboDefinitionUtil;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.impl.FakeUserServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemAnnotationBoboDefinitionScannerTest {

    @Test
    void scan_whenScanPackageWithOneItem_ReturnValidDefinitions() {
        List<BoboDefinition> actual = ItemAnnotationBoboDefinitionScanner.scan("items.dao.impl");
        assertEquals(1, actual.size());
        assertEquals(
                BoboDefinition.builder().boboName("fakeUserRepositoryImpl").boboClass(FakeUserRepositoryImpl.class).build(),
                actual.get(0)
        );
    }

    @Test
    void buildDefinition_whenCallBuildDefinition_returnValidBoboDefinition() {
        assertEquals(
                BoboDefinition.builder().boboName("fakeUserServiceImpl").boboClass(FakeUserServiceImpl.class).build(),
                BoboDefinitionUtil.buildDefinition(FakeUserServiceImpl.class)
        );
    }
}