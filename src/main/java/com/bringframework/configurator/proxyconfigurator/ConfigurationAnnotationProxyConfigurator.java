package com.bringframework.configurator.proxyconfigurator;

import com.bringframework.BoboRegistry;
import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import com.bringframework.definition.BoboDefinition;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

@Slf4j
public class ConfigurationAnnotationProxyConfigurator implements ProxyConfigurator {
    @Override
    public Object replaceWithProxyIfNeeded(Object configurationCandidate, Class<?> implClass, BoboRegistry registry) {
        if (implClass.isAnnotationPresent(Configuration.class)) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(implClass);
            enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                if (method.getName().equals("toString") || method.getName().equals("hashCode")) {
                    return proxy.invokeSuper(obj, args);
                }
                log.debug("Calling proxied configuration '{}' method", method.getName());
                String boboName = method.getAnnotation(Bobo.class).name();
                if (boboName.isEmpty()) {
                    boboName = method.getName();
                }
                if (registry.contains(boboName)) {
                    log.debug("Return proxy bobo '{}' from registry", boboName);
                    return registry.getBobo(boboName);
                }
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
