package init_method;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import items.service.impl.FakeFirstService;
import items.service.impl.FakeSecondService;
import items.service.impl.FakeThirdService;

@Configuration
public class FakeConfiguration {

    @Bobo
    public BoboWithoutInitMethod boboWithoutInitMethod() {
        return new BoboWithoutInitMethod();
    }

    @Bobo(initMethod = "someMethod")
    public BoboWithInitMethod boboWithInitMethod() {
        return new BoboWithInitMethod();
    }

    public static class BoboWithoutInitMethod {
    }

    public static class BoboWithInitMethod {
        private boolean initiatedInConstructor = false;
        private boolean wasChanged;

        public BoboWithInitMethod() {
            initiatedInConstructor = true;
        }

        public void someMethod() {
            System.out.println("Hooray from Bobo init method!");
            if (initiatedInConstructor) {
                wasChanged = true;
            }
        }

        public boolean isWasChanged() {
            return wasChanged;
        }
    }
}
