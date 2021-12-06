package com.bringframework.configurator;

import com.bringframework.BoboRegistry;
import com.bringframework.exception.BoboException;
import items.dao.FakeUserRepository;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.impl.FakeUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InjectAnnotationBoboConfiguratorTest {
    private BoboRegistry mockRegistry;

    @BeforeEach
    void setUp() {
        mockRegistry = mock(BoboRegistry.class);
    }

    @Test
    void configure_whenBoboFieldHasInjectAnnotation_shouldCallRegistryToGetBoboForThatField() {
        FakeUserRepositoryImpl repository = new FakeUserRepositoryImpl();
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenReturn(repository);

        InjectAnnotationBoboConfigurator configurator = new InjectAnnotationBoboConfigurator();
        FakeUserServiceImpl initialObject = new FakeUserServiceImpl();
        configurator.configure(initialObject, mockRegistry);

        assertNotNull(initialObject.getFakeUserRepository());
        assertEquals(repository, initialObject.getFakeUserRepository());
    }

    @Test
    void configure_whenExceptionWasThrown_wrapItToBoboException() {
        RuntimeException expectedCause = new RuntimeException("Expected");
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenThrow(expectedCause);

        InjectAnnotationBoboConfigurator configurator = new InjectAnnotationBoboConfigurator();

        BoboException actualException = assertThrows(
                BoboException.class,
                () -> configurator.configure(new FakeUserServiceImpl(), mockRegistry)
        );

        assertEquals("Error during configuring bobo object", actualException.getMessage());
        assertEquals(expectedCause, actualException.getCause());
    }

    @Test
    void configure_whenBoboExceptionWasThrown_reThrowIt() {
        BoboException expectedException = new BoboException("Expected");
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenThrow(expectedException);

        InjectAnnotationBoboConfigurator configurator = new InjectAnnotationBoboConfigurator();

        BoboException actualException = assertThrows(
                BoboException.class,
                () -> configurator.configure(new FakeUserServiceImpl(), mockRegistry)
        );

        assertEquals(expectedException, actualException);
    }

    @Test
    void configure_whenFieldNotAnnotatedInject_shouldNotCallRegistry() {
        InjectAnnotationBoboConfigurator configurator = new InjectAnnotationBoboConfigurator();
        configurator.configure(new FakeUserRepositoryImpl(), mockRegistry);

        verifyNoInteractions(mockRegistry);
    }
}