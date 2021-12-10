package com.bringframework.configurator;

import com.bringframework.BoboRegistry;

/**
 * <p>Common interface for configuring newly instantiated object by BoboFactory.
 * <p>Typical usage is set up object before putting it to context.
 * <p>Known implementations: {@link InjectAnnotationBoboConfigurator} and {@link BoboValueAnnotationConfiguration}
 * <p>Implementation should have default empty constructor.
 * <p> Bring user can have its own implementation if they want to add some logic for set up bobo
 *
 * @author Andrii Bobrov
 * @since 12 november 2021
 **/
public interface BoboConfigurator {
    /**
     * Method for setting up a Bobo instance.
     * Magic performs here!
     *
     * @param bobo     newly created bobo instance
     * @param registry {@link BoboRegistry}
     */
    void configure(Object bobo, BoboRegistry registry);
}
