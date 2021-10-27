package com.bring.project.service.processor.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PackageScanner {
    private static final Map<String, PackageScanner> injectors = new HashMap<>();
    private List<Class> classes = new ArrayList<>();

    public PackageScanner(String mainPackageName) {
        try {
            classes.addAll(getClassesByPackageName(mainPackageName));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Can't get information about all classes", e);
        }
    }

    public static PackageScanner getInstance(String mainPackageName) {
        if (injectors.containsKey(mainPackageName)) {
            return injectors.get(mainPackageName);
        }
        PackageScanner injector = new PackageScanner(mainPackageName);
        injectors.put(mainPackageName, injector);
        return injector;
    }


    private static List<Class> getClassesByPackageName(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new RuntimeException("ClassLoader is null");
        }
        String path = packageName.replace(".", "/");
        Enumeration<URL> classLoaderResources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        
        classLoaderResources.asIterator().forEachRemaining(resource -> dirs.add(new File(resource.getFile())));

        ArrayList<Class> classes = new ArrayList<>();

        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }


    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    if (fileName.contains(".")) {
                        throw new RuntimeException("File name shouldn't consist point.");
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
