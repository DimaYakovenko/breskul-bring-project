package com.bringframework.configurator;

import com.bringframework.BoboFactory;

public interface BoboConfigurator {
    void configure(Object bobo, BoboFactory registry);
}
