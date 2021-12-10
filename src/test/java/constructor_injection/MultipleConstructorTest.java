package constructor_injection;

import com.bringframework.BoboRegistry;
import com.bringframework.exception.BoboException;
import constructor_injection.successful_cases.MultiConstructor;
import constructor_injection.successful_cases.MultiConstructorConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultipleConstructorTest {

    private static final String POSITIVE_PACKAGE_TO_SCAN = "constructor_injection.successful_cases";
    private static final String NEGATIVE_PACKAGE_TO_SCAN = "constructor_injection.negative_cases";

    @Test
    void getBoboByType_whenGetBoboByType_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(POSITIVE_PACKAGE_TO_SCAN);
        // When
        MultiConstructor multiConstructorBobo = boboRegistry.getBobo(MultiConstructor.class);
        // Then
        assertNotNull(multiConstructorBobo);
    }

    @Test
    void getBoboByType_whenGetBoboDefinedInConfiguration_returnValidBobo() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(POSITIVE_PACKAGE_TO_SCAN);
        // When
        MultiConstructorConfiguration.MyBobo2 myBobo2 = boboRegistry.getBobo(MultiConstructorConfiguration.MyBobo2.class);
        // Then
        assertNotNull(myBobo2);
    }

    @Test
    void whenBoboHasNoDefaultConstructor_shouldThrowBoboException() {
        // Given
        BoboException boboException = assertThrows(BoboException.class, () -> new BoboRegistry(NEGATIVE_PACKAGE_TO_SCAN + ".item.case1"));

        assertEquals(
                "No default constructor found for type class constructor_injection.negative_cases.item.case1.NoDefaultConstructorItem",
                boboException.getMessage()
        );
    }

    @Test
    void whenBoboHasMoreThenOneInjectAnnotated_shouldThrowBoboException() {
        // Given
        BoboException boboException = assertThrows(BoboException.class, () -> new BoboRegistry(NEGATIVE_PACKAGE_TO_SCAN+ ".item.case2"));

        assertEquals(
                "Error creating bobo with name 'ambiguousConstructorItem': Invalid inject-marked constructor: " +
                        "public constructor_injection.negative_cases.item.case2.AmbiguousConstructorItem" +
                        "(constructor_injection.negative_cases.HelperBobo1). Found constructor with 'required' " +
                        "Inject annotation already: public constructor_injection.negative_cases.item.case2." +
                        "AmbiguousConstructorItem(constructor_injection.negative_cases.HelperBobo2)",
                boboException.getMessage()
        );
    }
}
