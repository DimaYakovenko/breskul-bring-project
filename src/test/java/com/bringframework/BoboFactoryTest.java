package com.bringframework;

import com.bringframework.configurator.BoboConfigurator;
import com.bringframework.configurator.BoboConfiguratorScanner;
import com.bringframework.configurator.InjectAnnotationBoboConfigurator;
import com.bringframework.definition.BoboDefinition;
import items.dao.FakeUserRepository;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.impl.FakeUserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static com.bringframework.configurator.BoboConfiguratorScanner.DEFAULT_PACKAGE;
import static java.beans.Introspector.decapitalize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class BoboFactoryTest {
    private static final String PACKAGE_TO_SCAN = "items";
    private BoboRegistry mockRegistry;

    @BeforeEach
    void setUp() {
        mockRegistry = mock(BoboRegistry.class);
    }

    @Test
    @SneakyThrows
    void shouldCreateBoboFromBoboDefinition() {
        // Given
        BoboFactory factory = new BoboFactory(mockRegistry, PACKAGE_TO_SCAN);
        Class<FakeUserServiceImpl> boboType = FakeUserServiceImpl.class;
        BoboDefinition boboDefinition = BoboDefinition.of(boboType, decapitalize(boboType.getSimpleName()));

        // When
        Object bobo = factory.createBobo(boboDefinition);

        // Then
        assertInstanceOf(FakeUserServiceImpl.class, bobo);
        Field actualRepository = bobo.getClass().getDeclaredField("fakeUserRepository");
        assertNotNull(actualRepository);
        assertEquals(FakeUserRepository.class.getSimpleName(), actualRepository.getType().getSimpleName());
    }

    @Test
    @SneakyThrows
    void createBobo_whenCreateBoboWithInnerDependency_shouldCallBoboRegistryGetBoboForThatDependency() {
        // Given
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenReturn(new FakeUserRepositoryImpl());
        BoboFactory factory = new BoboFactory(mockRegistry, PACKAGE_TO_SCAN);

        // When
        FakeUserServiceImpl userServiceBobo = (FakeUserServiceImpl) factory.createBobo(
                BoboDefinition.of(
                        FakeUserServiceImpl.class,
                        "myServiceImpl"
                )
        );

        // Then
        Field fakeUserRepository = userServiceBobo.getClass().getDeclaredField("fakeUserRepository");
        fakeUserRepository.setAccessible(true);
        assertEquals(FakeUserRepository.class.getSimpleName(), fakeUserRepository.getType().getSimpleName());
        assertNotNull(fakeUserRepository.get(userServiceBobo));
        verify(mockRegistry).getBobo(FakeUserRepository.class);
    }

    @Test
    void addBoboConfigurator_whenAddNewBoboConfigurator_itWasAddedToConfiguratorList() {
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenReturn(new FakeUserRepositoryImpl());

        List<BoboConfigurator> configurators = new ArrayList<>();
        MockedStatic<BoboConfiguratorScanner> scannerMock = Mockito.mockStatic(BoboConfiguratorScanner.class);
        scannerMock.when(() -> BoboConfiguratorScanner.scan(DEFAULT_PACKAGE)).thenReturn(configurators);
        BoboFactory factory = new BoboFactory(mockRegistry);
        InjectAnnotationBoboConfigurator boboConfigurator = new InjectAnnotationBoboConfigurator();
        factory.addBoboConfigurator(boboConfigurator);
        assertEquals(1, configurators.size());
        assertEquals(configurators.get(0), boboConfigurator);
    }

    @Test
    void addBoboConfigurator_whenAddNewBoboConfigurator_itShouldBeInvokedDuringObjectConfiguration() {
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenReturn(new FakeUserRepositoryImpl());

        BoboFactory factory = new BoboFactory(mockRegistry);
        AtomicBoolean wasInvoked = new AtomicBoolean();
        factory.addBoboConfigurator((obj, reg) -> wasInvoked.set(true));
        factory.createBobo(BoboDefinition.of(FakeUserRepositoryImpl.class, "fakeUserRepositoryImpl"));

        assertTrue(wasInvoked.get());
    }

}