package demonstration.project.dao.impl;

import com.bringframework.annotation.Dao;
import demonstration.project.dao.DaoInterface;

@Dao
public class DaoClass implements DaoInterface {

    @Override
    public String showMe() {
        return "It is alive!!!! \uD83D\uDE02 \uD83D\uDE02 \uD83D\uDE02";
    }
}
