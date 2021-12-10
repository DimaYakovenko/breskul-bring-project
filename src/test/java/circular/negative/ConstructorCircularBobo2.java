package circular.negative;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import lombok.Getter;

@Item
public class ConstructorCircularBobo2 {
    @Getter
    private ConstructorCircularBobo3 bobo3;

    @Inject
    public ConstructorCircularBobo2(ConstructorCircularBobo3 bobo3) {
        this.bobo3 = bobo3;
    }
}
