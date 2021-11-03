package demonstration.project.dao.impl;

import com.bringframework.annotation.Item;
import demonstration.project.dao.MyDao;

@Item
public class MyDaoImpl implements MyDao {

    @Override
    public String showMe() {
        return "It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02";
    }
}
