package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import items.dao.FakeUserRepository;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.impl.FakeUserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static java.beans.Introspector.decapitalize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoboFactoryTest {
    protected static final String PACKAGE_TO_SCAN = "items";
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
        BoboDefinition boboDefinition = BoboDefinition.builder()
                .boboClass(boboType)
                .boboName(decapitalize(boboType.getSimpleName()))
                .build();

        // When
        FakeUserServiceImpl bobo = (FakeUserServiceImpl) factory.createBobo(boboDefinition);

        // Then
        assertInstanceOf(FakeUserServiceImpl.class, bobo);
        Field fakeUserRepository = bobo.getClass().getDeclaredField("fakeUserRepository");
        assertEquals(FakeUserRepository.class.getSimpleName(), fakeUserRepository.getType().getSimpleName());
    }

    @Test
    @SneakyThrows
    void createBobo_whenCreateBoboWithInnerDependency_shouldCallBoboRegistryGetBoboForThatDependency() {
        // Given
        when(mockRegistry.getBobo(FakeUserRepository.class)).thenReturn(new FakeUserRepositoryImpl());
        BoboFactory factory = new BoboFactory(mockRegistry, PACKAGE_TO_SCAN);
        FakeUserServiceImpl userServiceBobo = (FakeUserServiceImpl) factory.createBobo(BoboDefinition.builder().boboClass(FakeUserServiceImpl.class).boboName("myServiceImpl").build());

        // When
        factory.configure(userServiceBobo);

        // Then
        Field fakeUserRepository = userServiceBobo.getClass().getDeclaredField("fakeUserRepository");
        fakeUserRepository.setAccessible(true);
        assertEquals(FakeUserRepository.class.getSimpleName(), fakeUserRepository.getType().getSimpleName());
        assertNotNull(fakeUserRepository.get(userServiceBobo));
        verify(mockRegistry).getBobo(FakeUserRepository.class);
    }

    @Test
    void addBoboConfigurator_whenAddNewBoboConfigurator_itWasAddedAndWasInvokedDuringObjectConfiguration() {
        when(mockRegistry.getBobo(MyDao.class)).thenReturn(new MyDaoImpl());

        BoboFactory factory = new BoboFactory(mockRegistry);
        int initConfiguratorSize = factory.getBoboConfigurators().size();
        AtomicBoolean wasInvoked = new AtomicBoolean();
        factory.addBoboConfigurator((obj, reg) -> wasInvoked.getAndSet(true));
        factory.createBobo(BoboDefinition.builder().boboClass(MyServiceImpl.class).boboName("myServiceImpl").build());

        assertEquals(initConfiguratorSize + 1, factory.getBoboConfigurators().size());
        assertTrue(wasInvoked.get());
    }

}