package com.bring.project;

import com.bring.project.service.processor.impl.PackageScanner;

public class App {
    public static void main(String[] args) {

        var packageScanner = PackageScanner.getInstance("com.bring.project");
    }
}
