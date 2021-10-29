package com.bring.project;

import com.bring.project.service.SerIn;
import com.bring.project.service.ServiceExample;
import com.bring.project.service.processor.impl.PackageScanner;

public class App {

    public static void main(String[] args) {

        var context = PackageScanner.scanPackage("com.bring.project");

        ServiceExample serviceExample = (ServiceExample) context.getBean(SerIn.class);

        System.out.println("IN main....");
        System.out.println(serviceExample.show());
    }
}
