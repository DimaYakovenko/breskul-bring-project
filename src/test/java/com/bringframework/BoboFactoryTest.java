package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import items.dao.FakeUserRepository;
import items.dao.impl.FakeUserRepositoryImpl;
import items.service.FakeUserService;
import items.service.impl.FakeUserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static java.beans.Introspector.decapitalize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoboFactoryTest extends BaseTest {

    private BoboRegistry mockRegistry;

    @BeforeEach
    void setUp() {
        mockRegistry = mock(BoboRegistry.class);
    }

    @Test
    @SneakyThrows
    void shouldCreateBoboFromBoboDefinition() {
        // Given
        BoboRegistry registry = Mockito.mock(BoboRegistry.class);
        BoboFactory factory = new BoboFactory(registry, PACKAGE_TO_SCAN);
        Class<FakeUserServiceImpl> boboType = FakeUserServiceImpl.class;
        BoboDefinition boboDefinition = BoboDefinition.builder()
                .boboClass(boboType)
                .boboName(decapitalize(boboType.getSimpleName()))
                .build();

        // When
        FakeUserService bobo = factory.createBobo(boboDefinition);

        // Then
        assertInstanceOf(FakeUserServiceImpl.class, bobo);
        Field fakeUserRepository = bobo.getClass().getDeclaredField("fakeUserRepository");
        assertEquals(FakeUserRepository.class.getSimpleName(), fakeUserRepository.getType().getSimpleName());
    }

    @Test
    @SneakyThrows
    void shouldConfigureBoboWithInjectedField() {
        // Given
        BoboRegistry registry = new BoboRegistry("does.not.exist");
        BoboFactory factory = new BoboFactory(registry, PACKAGE_TO_SCAN);
        Class<FakeUserServiceImpl> serviceBoboType = FakeUserServiceImpl.class;
        Class<FakeUserRepositoryImpl> repositoryBoboType = FakeUserRepositoryImpl.class;
        BoboDefinition userServiceBoboDefinition = BoboDefinition.builder()
                .boboClass(serviceBoboType)
                .boboName(decapitalize(serviceBoboType.getSimpleName()))
                .build();
        BoboDefinition userRepositoryBoboDefinition = BoboDefinition.builder()
                .boboClass(repositoryBoboType)
                .boboName(decapitalize(repositoryBoboType.getSimpleName()))
                .build();
        FakeUserService boboService = factory.createBobo(userServiceBoboDefinition);
        FakeUserRepository userRepo = factory.createBobo(userRepositoryBoboDefinition);
        registerBobo(registry, userRepositoryBoboDefinition, userRepo);

        // When
        factory.configure(boboService);

        // Then
        Field fakeUserRepository = boboService.getClass().getDeclaredField("fakeUserRepository");
        fakeUserRepository.setAccessible(true);
        assertEquals(FakeUserRepository.class.getSimpleName(), fakeUserRepository.getType().getSimpleName());
        assertNotNull(fakeUserRepository.get(boboService));
    }

    @Test
    void createBobo_whenCreateBoboFromValidBoboDefinition_returnValidObject() {
        BoboFactory factory = new BoboFactory(mockRegistry, "no-matter");
        MyDaoImpl myDaoImpl = (MyDaoImpl) factory.createBobo(BoboDefinition.builder().boboClass(MyDaoImpl.class).boboName("myDaoImpl").build());
        assertNotNull(myDaoImpl);
        assertEquals("It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02", myDaoImpl.showMe());
    }

    @Test
    void createBobo_whenCreateBoboWithInnerDependency_shouldCallBoboRegistryGetBoboForThatDependency() {
        when(mockRegistry.getBobo(MyDao.class)).thenReturn(new MyDaoImpl());
        BoboFactory factory = new BoboFactory(mockRegistry, "demonstration.project");
        MyServiceImpl myService = (MyServiceImpl) factory.createBobo(BoboDefinition.builder().boboClass(MyServiceImpl.class).boboName("myServiceImpl").build());
        assertNotNull(myService);
        assertEquals("It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02", myService.showMe());
        verify(mockRegistry, times(1)).getBobo(MyDao.class);
    }
}