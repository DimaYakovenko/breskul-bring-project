package com.bringframework;

import com.bringframework.definition.BoboDefinition;
import demonstration.project.dao.MyDao;
import demonstration.project.dao.impl.MyDaoImpl;
import demonstration.project.service.impl.MyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BoboFactoryTest {

    private BoboRegistry mockRegistry;

    @BeforeEach
    void setUp() {
        mockRegistry = mock(BoboRegistry.class);
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