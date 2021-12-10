package constructor_injection.successful_cases;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;

@Item
public class MultiConstructor {

    private ZeroConstructor zeroConstructor;

    @Inject
    public MultiConstructor(ZeroConstructor zeroConstructor) {
        this.zeroConstructor = zeroConstructor;
    }

}
