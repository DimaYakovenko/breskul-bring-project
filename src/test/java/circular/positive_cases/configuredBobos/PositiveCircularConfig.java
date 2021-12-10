package circular.positive_cases.configuredBobos;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Configuration
public class PositiveCircularConfig {

    @Bobo
    public CBobo1 bobo1() {
        return new CBobo1(bobo2());
    }

    @Bobo
    public CBobo2 bobo2() {
        return new CBobo2(bobo3());
    }

    @Bobo
    public CBobo3 bobo3() {
        return new CBobo3();
    }

    @Getter
    @AllArgsConstructor
    public static class CBobo1 {
        private CBobo2 bobo2;
    }

    @Getter
    @AllArgsConstructor
    public static class CBobo2 {
        private CBobo3 bobo3;
    }

    @Getter
    public static class CBobo3 {
    }
}
