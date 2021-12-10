package circular.negative_cases;

import com.bringframework.annotation.BoboValue;
import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import lombok.Getter;

@Item @Getter
public class ConstructorCircularBobo1 {
    private final ConstructorCircularBobo2 bobo2;
    @BoboValue("defaultValue")
    private String value;

    @Inject
    public ConstructorCircularBobo1(ConstructorCircularBobo2 bobo2) {
        this.bobo2 = bobo2;
    }
}
