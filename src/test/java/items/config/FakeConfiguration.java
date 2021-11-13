package items.config;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import items.service.impl.FakeFirstService;
import items.service.impl.FakeSecondService;
import items.service.impl.FakeThirdService;

@Configuration
public class FakeConfiguration {

    @Bobo(name = "firstServiceWithBobo")
    public FakeFirstService fakeFirstService() {
        return new FakeFirstService();
    }

    @Bobo
    public FakeSecondService fakeSecondService() {
        return new FakeSecondService();
    }

    public FakeThirdService fakeThirdService() {
        return new FakeThirdService();
    }
}
