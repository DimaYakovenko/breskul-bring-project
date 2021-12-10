package circular.negative;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import lombok.Getter;

@Item
public class ConstructorCircularBobo3 {
    @Getter
    private ConstructorCircularBobo1 bobo1;

    @Inject
    public ConstructorCircularBobo3(ConstructorCircularBobo1 bobo1) {
        this.bobo1 = bobo1;
    }
}
