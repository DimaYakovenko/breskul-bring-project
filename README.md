# Bring Framework

Brings the objects your application needs


### Bring project is a Dependency Injection Framework

## Features
* Inject classes with dependencies by `@Inject` annotation
* Get some values from properties file by `@BoboValue` annotation (Spring `@Value` analog)
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
To have this class injected, we'll need to create an BoboRegistry and configure it
properly:
```java
BoboRegistry boboRegistry = new BoboRegistry("package_to_scan");
// or by passing classes that have @Item annotation: 
BoboRegistry boboRegistry = new BoboRegistry(MyServiceImpl.class, MyDao.class);
// or create empty BoboRegistry and then call scan method for scanning item classes:
BoboRegistry boboRegistry = new BoboRegistry();
boboRegistry.scan("package_1", "package_2");

MyService bobo = boboRegistry.getBobo(MyService.class);
```
`@BoboValue` annotation allows you to get values from a properties file. 
This requires that a properties file (with name is `applcation.properties`)  be created in the user app in the resource directory. 
To get the value of properties user needs to mark the field with this annotation 
and in its parameter specify the name of the property. 
Example:
```java
@Item
public class MyService {
    
    @BoboValue("some.string.value")
    private String stringValue;
    ...    
}
```

@Bobo gives another possibility to create an object (Spring @Bean analog). Mark with this annotation only public methods.
To enable this feature class should be marked as @Configuration.
It is possible to set up a name for new bobo by @Bean(name = "yourName"). By default, it creates a bobo with name 
same as class with first letter in lower case. 

