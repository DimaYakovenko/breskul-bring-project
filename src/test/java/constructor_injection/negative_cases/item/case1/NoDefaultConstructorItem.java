package constructor_injection.negative_cases.item.case1;

import com.bringframework.annotation.Item;
import constructor_injection.negative_cases.HelperBobo1;

@Item
public class NoDefaultConstructorItem {
    public NoDefaultConstructorItem(HelperBobo1 helperBobo1) {
    }
}
