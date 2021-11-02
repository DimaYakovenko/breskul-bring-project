package com.bringframework.service.processor.impl;

import com.bringframework.annotation.Autowired;
import com.bringframework.annotation.Dao;
import com.bringframework.annotation.Service;
import com.bringframework.exception.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

public class PackageScanner {
    private static final Map<String, PackageScanner> EXISTING_CLASSES = new HashMap<>();
    private static final String REGEX_TARGET = ".";
    private static final String REGEX_REPLACEMENT = "/";
    private final Map<Class<?>, Object> instanceOfClasses = new HashMap<>();
    private final List<Class<?>> classes = new ArrayList<>();

    public PackageScanner(String mainPackageName) {
        Objects.requireNonNull(mainPackageName);
        try {
            classes.addAll(getClassesByPackageName(mainPackageName));
        } catch (IOException | ClassNotFoundException e) {
            throw new ScanPackageException("Can't get information about all classes", e);
        }
    }

    public static PackageScanner scanPackage(String mainPackageName) {
        if (EXISTING_CLASSES.containsKey(mainPackageName)) {
            return EXISTING_CLASSES.get(mainPackageName);
        }
        PackageScanner packageScanner = new PackageScanner(mainPackageName);
        EXISTING_CLASSES.put(mainPackageName, packageScanner);
        return packageScanner;
    }

    public Object getBean(Class<?> certainInterface) {
        Object newInstanceOfClass = null;
        Class<?> clazz = findClassExtendingInterface(certainInterface);
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields.length == 0) {
            return getNewInstance(clazz);
        }
        for (Field field : declaredFields) {
            if (field.getDeclaredAnnotation(Autowired.class) != null) {
                Object classToInject = getBean(field.getType());
                newInstanceOfClass = getNewInstance(clazz);
                setValueToField(field, newInstanceOfClass, classToInject);
            } else {
                throw new NotFoundAnnotationException("Class " + field.getName() + " in class "
                        + clazz.getName() + " hasn't annotation Autowired");
            }
        }

        return newInstanceOfClass;
    }

    private Class<?> findClassExtendingInterface(Class<?> certainInterface) {
        Class<?> correctClass = null;
        for (Class<?> clazz : classes) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> singleInterface : interfaces) {
                if (singleInterface.equals(certainInterface)
                        && (clazz.isAnnotationPresent(Service.class)
                        || clazz.isAnnotationPresent(Dao.class))) {
                    if (correctClass == null) {
                        correctClass = clazz;
                    } else {
                        throw new ScanPackageException("Two or more classes that implement interface "
                                + certainInterface.getName() + " has annotation Dao or Service)");
                    }
                }
            }
        }
        if (correctClass != null) {
            return correctClass;
        }
        throw new RuntimeException("Can't find class which implemented "
                + certainInterface.getName() + " interface with valid annotation (Dao or Service)");
    }

    private Object getNewInstance(Class<?> certainClass) {
        if (instanceOfClasses.containsKey(certainClass)) {
            return instanceOfClasses.get(certainClass);
        }
        try {
            Constructor<?> constructorDao = certainClass.getConstructor();
            Object newInstance = constructorDao.newInstance();
            instanceOfClasses.put(certainClass, newInstance);
            return newInstance;
        } catch (IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchMethodException e) {
            throw new CreateInstanceException("Can't create instance of the class", e);
        }
    }

    private void setValueToField(Field field, Object instanceOfClass, Object classToInject) {
        try {
            field.setAccessible(true);
            field.set(instanceOfClass, classToInject);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Can't set value to field ", e);
        }
    }


    /**
     * Scans all classes accessible from the context class loader which
     * belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws EmptyClassLoaderException if the ClassLoader is null
     * @throws ClassNotFoundException if the class cannot be located
     * @throws IOException            if I/O errors occur
     */
    private static List<Class<?>> getClassesByPackageName(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new EmptyClassLoaderException("ClassLoader is empty");
        }
        String path = packageName.replace(REGEX_TARGET, REGEX_REPLACEMENT);
        Enumeration<URL> classLoaderResources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();

        classLoaderResources.asIterator().forEachRemaining(resource -> dirs.add(new File(resource.getFile())));

        ArrayList<Class<?>> classes = new ArrayList<>();

        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }


    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws InvalidFileNameException if directory consist point "."
     * @throws ClassNotFoundException   if the class cannot be located
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    if (fileName.contains(".")) {
                        throw new InvalidFileNameException("File name shouldn't consist point \".\"");
                    }
                    classes.addAll(findClasses(file, packageName + "." + fileName));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + "." + fileName.substring(0, fileName.length() - 6)));
                }
            }
        }
        return classes;
    }
}
