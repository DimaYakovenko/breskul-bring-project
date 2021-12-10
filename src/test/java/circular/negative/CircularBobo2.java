package circular.negative;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import lombok.Getter;

@Item
public class CircularBobo2 {
    @Inject @Getter
    private CircularBobo3 bobo3;
}
