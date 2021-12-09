package constructor_injection;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import lombok.ToString;

@Configuration
public class MultiConstructorConfiguration {

    @Bobo
    public MyBoboI myBobo1() {
        return new MyBobo1();
    }

    @Bobo
    public MyBobo2 myBobo2(MyBoboI myBoboI, MultiConstructor multiConstructor) {
        return new MyBobo2(myBoboI, multiConstructor);
    }

    public interface MyBoboI {
    }

    @ToString
    public static class MyBobo1 implements MyBoboI {
        private MyBobo1(){}
    }

    @ToString
    public static class MyBobo2 {
        private final MyBoboI myBobo2;
        private final MultiConstructor multiConstructor;

        MyBobo2(MyBoboI myBobo2, MultiConstructor multiConstructor){
            this.myBobo2 = myBobo2;
            this.multiConstructor = multiConstructor;
        }
    }


}
