package demonstration.project.service.impl;

import com.bringframework.annotation.Autowired;
import com.bringframework.annotation.Service;
import demonstration.project.dao.DaoInterface;
import demonstration.project.service.ServiceInterface;

@Service
public class ServiceClass implements ServiceInterface {

    @Autowired
    private DaoInterface daoInterface;

    @Override
    public String showMe() {
        return daoInterface.showMe();
    }
}
