package constructor_injection.negative_cases.item.case2;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import constructor_injection.negative_cases.HelperBobo1;
import constructor_injection.negative_cases.HelperBobo2;

@Item
public class AmbiguousConstructorItem {
    @Inject
    public AmbiguousConstructorItem(HelperBobo1 helperBobo1) {
    }

    @Inject
    public AmbiguousConstructorItem(HelperBobo2 bobo2) {
    }
}
