package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import items.dao.FakeUserRepository;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.FakeUserService;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;

import static com.bringframework.exception.ExceptionErrorMessage.AMBIGUOUS_BOBO_ERROR;
import static com.bringframework.exception.ExceptionErrorMessage.NO_SUCH_BOBO_DEFINIITON_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoboRegistryTest extends BaseTest {

    @Test
    public void getBoboByType_whenGetBoboByType_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        FakeUserService fakeUserService = boboRegistry.getBobo(FakeUserService.class);
        // Then
        assertNotNull(fakeUserService);
    }

    @Test
    public void getBoboByType_whenGetTwoSingletonBobo_shouldReturnTheSameInstance() {
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
    public void getBoboByType_whenBoboDefinitionNotFound_throwNoSuchBoboDefinitionException() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry("does.not.exist");
        Class<FakeUserService> myBoboClass = FakeUserService.class;

        // When
        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> boboRegistry.getBobo(myBoboClass));

        // Then
        assertEquals(String.format(NO_SUCH_BOBO_DEFINIITON_ERROR, myBoboClass.getSimpleName()), exception.getMessage());
    }

    @Test
    public void getBoboByType_whenBoboDefinitionMoreThenOneFound_throwAmbiguousBoboDefinitionException() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);

        Class<FakeUserRepositoryImpl> secondUserRepoBoboType = FakeUserRepositoryImpl.class;
        String secondUserRepoBoboName = "fakeUserRepositoryImpl2";
        BoboDefinition userRepositoryBoboDefinition = BoboDefinition.builder()
                .boboClass(secondUserRepoBoboType)
                .boboName(secondUserRepoBoboName)
                .build();
        FakeUserRepository userRepo = new FakeUserRepositoryImpl();
        registerBobo(boboRegistry, userRepositoryBoboDefinition, userRepo);

        // When
        Class<FakeUserRepository> wantedBoboType = FakeUserRepository.class;
        AmbiguousBoboDefinitionException actualException =
                assertThrows(AmbiguousBoboDefinitionException.class, () -> boboRegistry.getBobo(wantedBoboType));

        // Then
        String expectedErrorMsg = String.format(AMBIGUOUS_BOBO_ERROR, wantedBoboType.getCanonicalName(), 2, String.join(", ", Introspector.decapitalize(secondUserRepoBoboType.getSimpleName()), secondUserRepoBoboName));
        assertEquals(expectedErrorMsg, actualException.getMessage());
    }
}