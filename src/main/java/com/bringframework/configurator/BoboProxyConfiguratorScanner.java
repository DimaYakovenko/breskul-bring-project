package com.bringframework.configurator;

import com.bringframework.configurator.proxyconfigurator.ProxyConfigurator;
import com.bringframework.exception.BoboException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BoboProxyConfiguratorScanner {

    public static List<ProxyConfigurator> scan(String... packageToScan) {
        log.info("Scan processing started for packages {}", (Object[]) packageToScan);
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
