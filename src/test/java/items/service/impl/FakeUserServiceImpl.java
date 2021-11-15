package items.service.impl;

import com.bringframework.annotation.Inject;
import com.bringframework.annotation.Item;
import items.dao.FakeUserRepository;
import items.service.FakeUserService;
import lombok.Getter;

@Item
public class FakeUserServiceImpl implements FakeUserService {
    @Inject
    @Getter
    private FakeUserRepository fakeUserRepository;
}
