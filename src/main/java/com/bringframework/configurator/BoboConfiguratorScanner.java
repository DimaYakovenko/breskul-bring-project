package com.bringframework.configurator;

import com.bringframework.exception.BoboException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for scanning packages for the implementation of {@link BoboConfigurator}
 *
 * @author Andrii Bobrov
 * @author Yuliia Smerechynska
 * @since 8 december 2021
 */
@Slf4j
@UtilityClass
public class BoboConfiguratorScanner {
    public static final String DEFAULT_PACKAGE = "com.bringframework.configurator";

    /**
     * Scan input packages for the {@link BoboConfigurator} implementations
     *
     * @param packageToScan array of packages where to find BoboConfigurators
     * @return list of found {@link BoboConfigurator} implementations
     */
    public static List<BoboConfigurator> scan(String... packageToScan) {
        log.info("Scan processing started for packages {}", (Object[]) packageToScan);
        return new Reflections(packageToScan, Scanners.SubTypes)
                .getSubTypesOf(BoboConfigurator.class)
                .stream()
                .map(BoboConfiguratorScanner::createInstance)
                .collect(Collectors.toList());
    }

    private static BoboConfigurator createInstance(Class<? extends BoboConfigurator> configuratorClass) {
        try {
            BoboConfigurator boboConfigurator = configuratorClass.getDeclaredConstructor().newInstance();
            log.debug("Create instance {} from declared constructor", boboConfigurator);
            return boboConfigurator;
        } catch (Exception e) {
            log.error("Can't create an instance of {} ", configuratorClass);
            throw new BoboException("Can't create an instance of " + configuratorClass);
        }
    }

}
