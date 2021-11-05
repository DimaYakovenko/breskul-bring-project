# Bring Framework
======================================
Brings the objects your application needs


### Bring project is a Dependency Injection Framework

## Features
* Inject classes with dependencies by `@Inject` annotation
* Register instances by marking them `@Item` annotation (Spring `@Component` analog)
* Register instances inside `@Configuration` class by `@Bobo` annotation on method  (Spring `@Bean` analog)

## Requirements
* Java Runtime Environment 11+ installed

# Getting started
A basic example will be used to illustrate how this framework works.

```java
@Item
public class MyService {

    @Inject
    private MyDao myDao;
    ...    
}
```
To have this class injected, we'll need to create an BoboFactory and configure it
properly:
```java
List<BoboDefinition> definitions = new ItemAnnotationBoboDefinitionScanner("package_to_scan").scan();
List<BoboConfigurator> configurators = new BoboConfiguratorScanner("package_to_scan").scan();

BoboFactory boboFactory = new BoboFactory(definitions, configurators);
MyService bobo = boboFactory.getBobo(MyService.class);
```