package com.bringframework.configurator.proxyconfigurator;

import com.bringframework.BoboRegistry;

/**
 * <p>Common interface as a last step of configuring newly created bobo
 * object before it will be putting to registry.
 * <p>Typical usage is replacing bobo instance by Proxy object if needed
 * <p>Known implementations: {@link ConfigurationAnnotationProxyConfigurator}}
 * <p>Implementation should have default empty constructor
 *
 * @author Andrii Bobrov
 * @since 8 december 2021
 **/
public interface ProxyConfigurator {
    /**
     * Method for replacing input object by proxy if needed.
     *
     * @param bobo      newly created bobo instance
     * @param implClass original bobo type
     * @param registry  {@link BoboRegistry}
     * @return Object instance itself or Proxy instance
     */
    Object replaceWithProxyIfNeeded(Object bobo, Class<?> implClass, BoboRegistry registry);
}
