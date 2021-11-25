package items.service.impl;

import com.bringframework.annotation.BoboValue;
import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import items.dao.FakeUserRepository;
import items.service.FakeUserService;
import lombok.Getter;

@Item
@Getter
public class FakeUserServiceImpl implements FakeUserService {
    @Inject
    private FakeUserRepository fakeUserRepository;

    @BoboValue("some.string.value")
    private String stringValue;

    @BoboValue("some.int.value")
    private int intValue;

    @BoboValue()
    private String defaultValue;
}
