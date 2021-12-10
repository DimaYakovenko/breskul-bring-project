package init_method;

import com.bringframework.BoboRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitMethodTest {

    private static final String PACKAGE_TO_SCAN = "init_method";

    @Test
    void whenBoboWithInitMethodDeclared_shouldInvokeIt() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        FakeConfiguration.BoboWithInitMethod boboWithInitMethod = boboRegistry.getBobo(FakeConfiguration.BoboWithInitMethod.class);
        // Then
        assertNotNull(boboWithInitMethod);
        assertTrue(boboWithInitMethod.isWasChanged());
    }

    @Test
    void whenItemWithInitMethodDeclared_shouldInvokeIt() {
        // Given
        BoboRegistry boboRegistry = new BoboRegistry(PACKAGE_TO_SCAN);
        // When
        ItemWithInit itemWithInit = boboRegistry.getBobo(ItemWithInit.class);
        // Then
        assertNotNull(itemWithInit);
        assertTrue(itemWithInit.wasChanged());
    }
}
