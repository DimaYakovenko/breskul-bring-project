package com.bringframework.configurator;

import com.bringframework.configurator.proxyconfigurator.ProxyConfigurator;
import com.bringframework.exception.BoboException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for scanning packages for the implementation of {@link ProxyConfigurator}
 *
 * @author Andrii Bobrov
 * @since 8 december 2021
 */
@Slf4j
@UtilityClass
public class BoboProxyConfiguratorScanner {

    /**
     * Scan input packages for the {@link ProxyConfigurator} implementations
     *
     * @param packageToScan array of packages where to find ProxyConfigurators
     * @return list of found {@link ProxyConfigurator} implementations
     */
    public static List<ProxyConfigurator> scan(String... packageToScan) {
        log.debug("Scan processing started for packages {}", (Object[]) packageToScan);
        return new Reflections(packageToScan, Scanners.SubTypes)
                .getSubTypesOf(ProxyConfigurator.class)
                .stream()
                .map(BoboProxyConfiguratorScanner::createInstance)
                .collect(Collectors.toList());
    }

    private static ProxyConfigurator createInstance(Class<? extends ProxyConfigurator> configuratorClass) {
        try {
            ProxyConfigurator boboConfigurator = configuratorClass.getDeclaredConstructor().newInstance();
            log.debug("Create instance {} from declared constructor", boboConfigurator);
            return boboConfigurator;
        } catch (Exception e) {
            log.error("Can't create an instance of {} ", configuratorClass);
            throw new BoboException("Can't create an instance of " + configuratorClass);
        }
    }

}

