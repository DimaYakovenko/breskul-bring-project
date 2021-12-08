package com.bringframework.configurator.proxyconfigurator;

import com.bringframework.BoboRegistry;

public interface ProxyConfigurator {
    Object replaceWithProxyIfNeeded(Object bobo, Class<?> implClass, BoboRegistry registry);
}
