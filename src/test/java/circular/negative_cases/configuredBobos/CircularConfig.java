package circular.negative_cases.configuredBobos;

import com.bringframework.annotation.Bobo;
import com.bringframework.annotation.Configuration;
import lombok.AllArgsConstructor;

@Configuration
public class CircularConfig {

    @Bobo
    public CBobo1 bobo1(CBobo2 bobo2) {
        return new CBobo1(bobo2);
    }

    @Bobo
    public CBobo2 bobo2(CBobo3 bobo3) {
        return new CBobo2(bobo3);
    }

    @Bobo
    public CBobo3 bobo3(CBobo1 bobo1) {
        return new CBobo3(bobo1);
    }

    @AllArgsConstructor
    public static class CBobo1 {
        private CBobo2 bobo2;
    }

    @AllArgsConstructor
    public static class CBobo2 {
        private CBobo3 bobo3;
    }
    @AllArgsConstructor
    public static class CBobo3 {
        private CBobo1 bobo1;

    }
}
