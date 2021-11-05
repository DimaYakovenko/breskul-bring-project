package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.BoboException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import demonstration.project.dao.impl.MyDaoImpl;
import demonstration.project.service.MyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoboFactoryTest {

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
