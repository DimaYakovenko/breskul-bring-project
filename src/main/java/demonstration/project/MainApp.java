package demonstration.project;

import com.bring.library.service.processor.impl.PackageScanner;
import demonstration.project.service.ServiceInterface;

public class MainApp {
    public static void main(String[] args) {

        var context = PackageScanner.scanPackage("demonstration.project");

        ServiceInterface serviceInterface = (ServiceInterface) context.getBean(ServiceInterface.class);

        System.out.println(serviceInterface.showMe());

    }
}
