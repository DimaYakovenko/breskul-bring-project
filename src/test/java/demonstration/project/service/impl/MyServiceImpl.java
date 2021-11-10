package demonstration.project.service.impl;

import com.bringframework.annotation.BoboValue;
import com.bringframework.annotation.Item;
import com.bringframework.annotation.Inject;
import demonstration.project.dao.MyDao;
import demonstration.project.service.MyService;

@Item
public class MyServiceImpl implements MyService {

    @Inject
    private MyDao myDao;

    @BoboValue("some.string.value")
    private String stringValue;

    @BoboValue("some.int.value")
    private int intValue;

    @BoboValue
    private String defaultValue;

    @Override
    public String showMe() {
        return myDao.showMe();
    }
}
