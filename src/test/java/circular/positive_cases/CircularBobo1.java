package circular.positive_cases;

import com.bringframework.annotation.BoboValue;
import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import lombok.Getter;

@Item @Getter
public class CircularBobo1 {
    @Inject
    private CircularBobo2 bobo2;
    @BoboValue("defaultValue")
    private String value;
}
