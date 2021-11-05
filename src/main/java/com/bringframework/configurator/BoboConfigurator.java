package com.bringframework.configurator;

import com.bringframework.BoboRegistry;

public interface BoboConfigurator {
    void configure(Object bobo, BoboRegistry registry);
}
