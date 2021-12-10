package circular.negative;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import lombok.Getter;

@Item
public class CircularBobo3 {
    @Inject @Getter
    private CircularBobo1 bobo1;
}
