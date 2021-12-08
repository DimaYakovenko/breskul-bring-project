package items.config;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import items.service.impl.*;

import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class InjectingInterBeanDependenciesConfiguration {

    private final AtomicBoolean wasCalled = new AtomicBoolean();

    @Bobo
    public Fake5Service fake5Service() {
        if (wasCalled.getAndSet(true)) {
            throw new IllegalStateException("Should call only once");
        }
        return new Fake5Service();
    }

    @Bobo
    public Fake4Service fake4Service() {
        return new Fake4Service(fake5Service());
    }

    @Bobo
    public Fake4Service fake42Service() {
        return new Fake4Service(fake5Service());
    }

}
