package circular;

import circular.negative_cases.*;
import circular.positive_cases.CircularBobo1;
import circular.positive_cases.CircularBobo2;
import circular.positive_cases.CircularBobo3;
import circular.positive_cases.configuredBobos.PositiveCircularConfig;
import com.bringframework.BoboRegistry;
import com.bringframework.exception.BoboException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CircularDependenciesTest {

    @Test
    void whenItemsHaveCircularDependenciesInFields_shouldResolveDependencies() {
        BoboRegistry boboRegistry = new BoboRegistry(CircularBobo1.class, CircularBobo2.class, CircularBobo3.class);
        var bobo1 = boboRegistry.getBobo(CircularBobo1.class);
        assertNotNull(bobo1);
        assertEquals("DefaultValue", bobo1.getValue());
        assertNotNull(bobo1.getBobo2());
        assertNotNull(bobo1.getBobo2().getBobo3());
    }

    @Test
    void whenConfigurationBobosHaveCircularDependenciesInFields_shouldResolveDependencies() {
        var boboRegistry = new BoboRegistry("circular.positive_cases.configuredBobos");
        var bobo1 = boboRegistry.getBobo(PositiveCircularConfig.CBobo1.class);
        assertNotNull(bobo1);
    }

    @Test
    void whenItemsHaveCircularDependenciesInConstructors_shouldFailsGracefullyWithThrowBoboException() {
        BoboException exception = assertThrows(BoboException.class, () -> new BoboRegistry(ConstructorCircularBobo1.class, ConstructorCircularBobo2.class, ConstructorCircularBobo3.class));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.startsWith("Bobos have circular dependencies between classes: "));
        assertTrue(actualMessage.contains("ConstructorCircularBobo1"));
        assertTrue(actualMessage.contains("ConstructorCircularBobo2"));
        assertTrue(actualMessage.contains("ConstructorCircularBobo3"));
    }

    @Test
    void whenConfigurationBobosHaveCircularDependenciesInConstructors_shouldFailsGracefullyWithThrowBoboException() {
        BoboException exception = assertThrows(BoboException.class, () -> new BoboRegistry("circular.negative_cases.configuredBobos"));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.startsWith("Bobos have circular dependencies between classes: "));
        assertTrue(actualMessage.contains("CBobo1"));
        assertTrue(actualMessage.contains("CBobo2"));
        assertTrue(actualMessage.contains("CBobo3"));
    }
}
