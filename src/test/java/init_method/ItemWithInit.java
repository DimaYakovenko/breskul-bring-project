package init_method;

import com.bringframework.annotation.InitMethod;
import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;

@Item
public class ItemWithInit {
    @Inject
    private ItemWithoutInit itemWithoutInit;
    private boolean wasChanged;

    @InitMethod
    public void init() {
        if (itemWithoutInit == null) {
            throw new IllegalStateException("Inner dependency should be initialized");
        }
        wasChanged = true;
        System.out.println("Hooray from Item Init method!");
    }

    public boolean wasChanged() {
        return wasChanged;
    }
}
