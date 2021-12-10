package com.bringframework.configurator.proxyconfigurator;

import com.bringframework.BoboRegistry;
import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import com.bringframework.definition.BoboDefinition;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * <p> ProxyConfigurator class for replacing classes annotated with {@link Configuration} by proxy
 * <p>Implementation of {@link ProxyConfigurator} interface.
 *
 * @author Andrii Bobrov
 * @since 8 december 2021
 **/
@Slf4j
public class ConfigurationAnnotationProxyConfigurator implements ProxyConfigurator {
    /**
     * If `configurationCandidate` is annotated with {@link Configuration} it will be replaced
     * by CGLIB Enhancer, so we can resolve Injecting Inter-bean Dependencies
     *
     * @param configurationCandidate bobo instance
     * @param implClass              original bobo type
     * @param registry               {@link BoboRegistry}
     * @return proxy instance
     */
    @Override
    public Object replaceWithProxyIfNeeded(Object configurationCandidate, Class<?> implClass, BoboRegistry registry) {
        if (implClass.isAnnotationPresent(Configuration.class)) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(implClass);
            enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                if (!method.isAnnotationPresent(Bobo.class)) {
                    return proxy.invokeSuper(obj, args);
                }
                String boboName = method.getAnnotation(Bobo.class).name();
                if (boboName.isEmpty()) {
                    boboName = method.getName();
                }
                if (registry.contains(boboName)) {
                    log.debug("Return cached bobo '{}' from registry", boboName);
                    return registry.getBobo(boboName);
                }
                log.debug("Calling the proxy configuration '{}' method", method.getName());
                Object newBoboObj = proxy.invokeSuper(obj, args);
                BoboDefinition boboDefinition = registry.getBoboDefinition(boboName);
                registry.putBobo(boboDefinition, newBoboObj);
                return newBoboObj;
            });
            return enhancer.create();
        }
        return configurationCandidate;
    }
}
