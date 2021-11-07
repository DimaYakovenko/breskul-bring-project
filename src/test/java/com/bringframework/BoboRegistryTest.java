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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void getBoboByType_whenGetBoboByType_returnValidBobo() {

        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");

        MyService myService = boboRegistry.getBobo(MyService.class);
        assertNotNull(myService);
        assertEquals("It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02", myService.showMe());
    }

    @Test
    public void getBoboByType_whenGetTwoSingletonBobo_shouldReturnTheSameInstance() {
        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");

        MyService bobo = boboRegistry.getBobo(MyService.class);
        assertNotNull(bobo);
        MyService bobo2 = boboRegistry.getBobo(MyService.class);
        assertNotNull(bobo2);
        assertEquals(bobo, bobo2);
    }

    @Test
    public void getBoboByType_whenBoboDefinitionNotFound_throwNoSuchBoboDefinitionException() {
        BoboRegistry boboRegistry = new BoboRegistry("not.exists");
        Class<MyService> myBoboClass = MyService.class;

        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> {
            boboRegistry.getBobo(myBoboClass);
        });

        assertEquals("No such bobo definition for type '" + myBoboClass.getSimpleName() + "'", exception.getMessage());
    }

    @Test
    public void getBoboByType_whenBoboDefinitionMoreThenOneFound_throwAmbiguousBoboDefinitionException() {
        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");
        boboRegistry.registerBoboDefinition(BoboDefinition.builder().boboName("dao").boboClass(MyDaoImpl.class).build());

        AmbiguousBoboDefinitionException actualException = assertThrows(AmbiguousBoboDefinitionException.class, () -> {
            boboRegistry.getBobo(MyDaoImpl.class);
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
        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");
        boboRegistry.registerBoboDefinition(BoboDefinition.builder().boboName("myDaoImpl1").boboClass(MyDaoImpl.class).build());

        BoboException actualException = assertThrows(BoboException.class, () -> {
            boboRegistry.getBobo(MyService.class);
        });

        assertEquals("Cannot create bobo: myServiceImpl", actualException.getMessage());
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
    public void getBobo_whenGetBoboByNameWithClass_returnValidBobo() {

        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");

        MyService myService = boboRegistry.getBobo("myServiceImpl", MyService.class);
        assertNotNull(myService);
        assertEquals("It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02", myService.showMe());
    }

    @Test
    public void getBobo_whenGetBoboByName_returnValidBobo() {

        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");

        MyService myService = (MyService) boboRegistry.getBobo("myServiceImpl");
        assertNotNull(myService);
    }

    @Test
    public void getBobo_whenGetNonExistingBoboByName_returnValidBobo() {

        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");
        BoboException actualException = assertThrows(NoSuchBoboDefinitionException.class, () ->
                boboRegistry.getBobo("notExists"));
        assertEquals("No such bobo definition: 'notExists'", actualException.getMessage());
    }

    @Test
    public void containsBobo_whenSearchForExistingBoboByName_returnTrue() {
        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");
        boboRegistry.getBobo(MyDaoImpl.class);//initialize
        assertTrue(boboRegistry.containsBobo("myDaoImpl"));
    }

    @Test
    public void containsBobo_whenSearchForNonExistingBoboByName_returnFalse() {
        BoboRegistry boboRegistry = new BoboRegistry("demonstration.project");
        assertFalse(boboRegistry.containsBobo("notExist"));
    }

    @Test
    public void register_whenRegisterNewItemClass_shouldAddNewBoboDefinition() {
        BoboRegistry boboRegistry = new BoboRegistry("ru");
        assertFalse(boboRegistry.containsBobo("myDaoImpl"));
        boboRegistry.register(MyDaoImpl.class);
        boboRegistry.getBobo(MyDaoImpl.class);
        assertTrue(boboRegistry.containsBobo("myDaoImpl"));
    }
}