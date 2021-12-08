package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.BoboException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import com.bringframework.util.BoboDefinitionUtil;
import items.dao.FakeUserRepository;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.FakeUserService;
import items.service.impl.FakeFirstService;
import items.service.impl.FakeSecondService;
import items.service.impl.FakeThirdService;
import items.service.impl.FakeUserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import static com.bringframework.exception.ExceptionErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;

class BoboRegistryTest {

    private static final String PACKAGE_TO_SCAN = "items";

    @Test
    void getBoboByType_whenGetBoboByType_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        FakeUserService fakeUserService = boboRegistry.getBobo(FakeUserService.class);
        // Then
        assertNotNull(fakeUserService);
    }

    @Test
    void getBoboByType_whenGetTwoSingletonBobo_shouldReturnTheSameInstance() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);

        // When
        FakeUserService bobo = boboRegistry.getBobo(FakeUserService.class);
        FakeUserService bobo2 = boboRegistry.getBobo(FakeUserService.class);

        //Then
        assertNotNull(bobo);
        assertNotNull(bobo2);
        assertEquals(bobo, bobo2);
    }

    @Test
    void getBoboByType_whenBoboDefinitionNotFound_throwNoSuchBoboDefinitionException() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry("does.not.exist");
        Class<FakeUserService> myBoboClass = FakeUserService.class;

        // When
        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> boboRegistry.getBobo(myBoboClass));

        // Then
        assertEquals(String.format(NO_SUCH_BOBO_DEFINITION_BY_TYPE_EXCEPTION, myBoboClass.getSimpleName()), exception.getMessage());
    }

    @Test
    void getBoboByType_whenBoboDefinitionMoreThenOneFound_throwAmbiguousBoboDefinitionException() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);

        Class<FakeUserRepositoryImpl> secondUserRepoBoboType = FakeUserRepositoryImpl.class;
        String secondUserRepoBoboName = "fakeUserRepositoryImpl2";
        BoboDefinition userRepositoryBoboDefinition = BoboDefinition.of(secondUserRepoBoboType, secondUserRepoBoboName);
        FakeUserRepository userRepo = new FakeUserRepositoryImpl();
        registerBobo(boboRegistry, userRepositoryBoboDefinition, userRepo);

        // When
        Class<FakeUserRepository> wantedBoboType = FakeUserRepository.class;
        AmbiguousBoboDefinitionException actualException =
                assertThrows(AmbiguousBoboDefinitionException.class, () -> boboRegistry.getBobo(wantedBoboType));

        // Then
        String expectedErrorMsg = String.format(AMBIGUOUS_BOBO_EXCEPTION, wantedBoboType.getCanonicalName(), 2, "");
        assertEquals(expectedErrorMsg, actualException.getMessage().substring(0, 112));
        assertTrue(actualException.getMessage().contains(Introspector.decapitalize(secondUserRepoBoboType.getSimpleName())));
        assertTrue(actualException.getMessage().contains(secondUserRepoBoboName));
    }

    @Test
    void getBobo_whenGetBoboByNameWithClass_returnValidCastedBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        Object myService = boboRegistry.getBobo("fakeUserServiceImpl", FakeUserService.class);
        // Then
        assertNotNull(myService);
        assertInstanceOf(FakeUserServiceImpl.class, myService);
    }

    @Test
    void getBobo_whenGetBoboByName_returnValidRawObjectBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        Object myService = boboRegistry.getBobo("fakeUserServiceImpl");
        // Then
        assertNotNull(myService);
        assertInstanceOf(FakeUserService.class, myService);
    }

    @Test
    void getBobo_whenGetNonExistingBoboByName_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        String notExistsBoboName = "notExists";
        // When
        BoboException actualException = assertThrows(NoSuchBoboDefinitionException.class, () -> boboRegistry.getBobo(notExistsBoboName));
        // Then
        assertEquals(String.format(NO_SUCH_BOBO_DEFINITION_BY_NAME_EXCEPTION, notExistsBoboName), actualException.getMessage());
    }

    @Test
    void getBobo_whenManuallyAddedItemClassesInConstructor_shouldPutThemInRegistry() {
        BoboRegistry boboRegistry = new BoboRegistry(FakeUserRepositoryImpl.class, FakeUserServiceImpl.class);
        assertNotNull(boboRegistry.getBobo("fakeUserRepositoryImpl"));
        assertNotNull(boboRegistry.getBobo("fakeUserServiceImpl"));
    }

    @Test
    void getBobo_whenGetConfigurationBobo_shouldReturnValidBobo() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        assertNotNull(boboRegistry.getBobo("fakeConfiguration"));
    }

    @Test
    void getBobo_whenNameSpecifiedInAnnotation_shouldReturnValidBoboByName() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        FakeFirstService getBoboByName = (FakeFirstService) boboRegistry.getBobo("firstServiceWithBobo");
        assertNotNull(getBoboByName);
    }

    @Test
    void getBobo_whenAnnotationWithoutSpecifiedName_shouldReturnValidBoboByName() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        String boboName = BoboDefinitionUtil.generateBoboName(FakeSecondService.class);
        FakeSecondService getBoboByName = (FakeSecondService) boboRegistry.getBobo(boboName);
        assertNotNull(getBoboByName);
    }

    @Test
    void getBobo_whenNoBoboAnnotationOnMethod_shouldNotCreateBoboByNameAndThrowNoSuchBoboDefinitionException() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        String boboName = BoboDefinitionUtil.generateBoboName(FakeThirdService.class);
        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> boboRegistry.getBobo(boboName));
        assertEquals(String.format(NO_SUCH_BOBO_DEFINITION_BY_NAME_EXCEPTION, boboName), exception.getMessage());
    }

    @Test
    void getBobo_whenNoBoboAnnotationOnMethod_shouldNotCreateBoboByTypeAndThrowNoSuchBoboDefinitionException() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        Class<FakeThirdService> nonExistingBoboType = FakeThirdService.class;
        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> boboRegistry.getBobo(nonExistingBoboType));
        assertEquals(String.format(NO_SUCH_BOBO_DEFINITION_BY_TYPE_EXCEPTION, nonExistingBoboType.getSimpleName()), exception.getMessage());
    }

    @Test
    void getBobo_whenAnnotatedWithBobo_shouldReturnValidBoboByType() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        FakeFirstService getBoboByType = boboRegistry.getBobo(FakeFirstService.class);
        assertNotNull(getBoboByType);
    }

    @Test
    void getBobo_whenAnnotatedBoboWithSpecifiedName_shouldReturnSameObjectByNameAndType() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        FakeFirstService getBoboByType = boboRegistry.getBobo(FakeFirstService.class);
        FakeFirstService getBoboByName = (FakeFirstService) boboRegistry.getBobo("firstServiceWithBobo");
        assertNotNull(getBoboByType);
        assertNotNull(getBoboByName);
        assertSame(getBoboByName, getBoboByType);
    }

    @Test
    void getBobo_whenAnnotatedBoboWithoutName_shouldReturnSameObjectByNameAndType() {
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        FakeSecondService getBoboByType = boboRegistry.getBobo(FakeSecondService.class);
        FakeSecondService getBoboByName = (FakeSecondService) boboRegistry.getBobo("fakeSecondService");
        assertNotNull(getBoboByType);
        assertNotNull(getBoboByName);
        assertSame(getBoboByName, getBoboByType);
    }

    @SneakyThrows
    private <T> void registerBobo(BoboRegistry registry, BoboDefinition boboDefinition, T bobo) {
        Field registryMapField = registry.getClass().getDeclaredField("registry");
        registryMapField.setAccessible(true);
        ConcurrentHashMap<BoboDefinition, Object> registryMap = (ConcurrentHashMap<BoboDefinition, Object>) registryMapField.get(registry);
        registryMap.put(boboDefinition, bobo);
    }
}