package com.bringframework.util;

import com.bringframework.definition.BoboDefinition;
import items.service.impl.FakeFirstService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoboDefinitionUtilTest {

    @Test
    void generateBoboName_whenClassName_shouldReturnSameNameWithFirstLetterInLowerCase() {
        String actual = BoboDefinitionUtil.generateBoboName(FakeFirstService.class);
        assertEquals("fakeFirstService", actual);
    }

    @Test
    void buildDefinition_whenPassOnlyType_shouldReturnValidBoboDefinition() {
        Class<FakeFirstService> type = FakeFirstService.class;
        BoboDefinition expected = BoboDefinition.of(type, "fakeFirstService");
        BoboDefinition actual = BoboDefinitionUtil.buildDefinition(type);
        assertEquals(expected, actual);
    }

    @Test
    void buildDefinition_whenPassTypeWithName_shouldReturnValidBoboDefinition() {
        Class<FakeFirstService> type = FakeFirstService.class;
        String boboName = "myService";
        BoboDefinition expected = BoboDefinition.of(type, boboName);
        BoboDefinition actual = BoboDefinitionUtil.buildDefinition(type, boboName);
        assertEquals(expected, actual);
    }
}