package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.InjectAnnotationBoboConfigurator;
import com.bringframework.definition.BoboDefinition;
import com.bringframework.definition.ItemAnnotationBoboDefinitionScanner;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.exception.AmbiguousBoboDefinitionException;
import com.bringframework.exception.BoboException;
import com.bringframework.exception.NoSuchBoboDefinitionException;
import demonstration.project.dao.impl.MyDaoImpl;
import demonstration.project.service.MyService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

public class BoboFactoryTest {

    @Test
    public void getBoboByTypeTest() {
        List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
        List<BoboConfigurator> configurators = new BoboConfiguratorScanner("demonstration.project").scan();

        BoboFactory boboFactory = new BoboFactory(definitions, configurators);

        MyService bobo = boboFactory.getBobo(MyService.class);
        assertNotNull(bobo);
        MyService bobo2 = boboFactory.getBobo(MyService.class);
        assertNotNull(bobo2);
        assertEquals(bobo.hashCode(), bobo2.hashCode());
        assertEquals("It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02", bobo.showMe());
    }

    @Test
    public void getBoboByType_whenBoboDefinitionNotFound_throwNoSuchBoboDefinitionException() {
        Exception exception = assertThrows(NoSuchBoboDefinitionException.class, () -> {
            BoboFactory boboFactory = new BoboFactory(emptyList(), emptyList());
            boboFactory.getBobo(MyService.class);
        });

        assertEquals("No such bobo definition for type 'MyService'", exception.getMessage());
    }

    @Test
    public void getBoboByType_whenBoboDefinitionMoreThenOneFound_throwAmbiguousBoboDefinitionException() {
        AmbiguousBoboDefinitionException actualException = assertThrows(AmbiguousBoboDefinitionException.class, () -> {
            List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
            definitions.add(BoboDefinition.builder().boboName("dao").boboClass(MyDaoImpl.class).build());
            BoboFactory boboFactory = new BoboFactory(definitions, List.of(new InjectAnnotationBoboConfigurator()));
            boboFactory.getBobo(MyDaoImpl.class);
        });
        assertEquals(
                "No qualifying bobo of type 'demonstration.project.dao.impl.MyDaoImpl' available: expected single matching bobo but found 2:",
                actualException.getMessage().substring(0, 123)
        );
        assertTrue(actualException.getMessage().substring(124).contains("dao"));
        assertTrue(actualException.getMessage().substring(124).contains("myDaoImpl"));
    }

    @Test
    public void getBoboByType_whenBoboDefinitionMoreThenOneFound_throwBoboExceptionWithNestedAmbiguousBoboDefinitionException() {
        BoboException actualException = assertThrows(BoboException.class, () -> {
            List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("demonstration.project").scan();
            definitions.add(BoboDefinition.builder().boboName("myDaoImpl1").boboClass(MyDaoImpl.class).build());
            BoboFactory boboFactory = new BoboFactory(definitions, List.of(new InjectAnnotationBoboConfigurator()));
            boboFactory.getBobo(MyService.class);
        });

        assertEquals(
                "Cannot instantiate bobo: myServiceImpl",
                actualException.getMessage()
        );
        assertTrue(actualException.getCause() instanceof AmbiguousBoboDefinitionException);
        assertEquals(
                "No qualifying bobo of type 'demonstration.project.dao.MyDao' available: expected single matching bobo but found 2:",
        actualException.getCause().getMessage().substring(0, 114)
        );
        assertTrue(actualException.getCause().getMessage().substring(114).contains("myDaoImpl"));
        assertTrue(actualException.getCause().getMessage().substring(114).contains("myDaoImpl1"));
    }
}
