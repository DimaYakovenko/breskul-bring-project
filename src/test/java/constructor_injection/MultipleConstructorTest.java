package constructor_injection;

import com.bringframework.BoboRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MultipleConstructorTest {

    private static final String PACKAGE_TO_SCAN = "constructor_injection";

    @Test
    void getBoboByType_whenGetBoboByType_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        MultiConstructor multiConstructorBobo = boboRegistry.getBobo(MultiConstructor.class);
        // Then
        assertNotNull(multiConstructorBobo);
    }

    @Test
    void getBoboByType_whenGetBoboDefinedInConfiguration_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        MultiConstructorConfiguration.MyBobo2 myBobo2 = boboRegistry.getBobo(MultiConstructorConfiguration.MyBobo2.class);
        // Then
        assertNotNull(myBobo2);
    }
}
