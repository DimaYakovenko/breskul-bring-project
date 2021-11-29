package items.broken.service;

import com.bringframework.annotation.BoboValue;
import com.bringframework.annotation.Item;
import items.model.FakeUser;

@Item
public class InvalidPropertyClassCastService {

    @BoboValue("class.cast.value")
    private FakeUser fakeUser;
}
