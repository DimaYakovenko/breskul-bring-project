package com.bring.project.service;

import com.bring.project.annotation.Autowired;
import com.bring.project.annotation.Service;
import com.bring.project.dao.DaoIn;

@Service
public class ServiceExample implements SerIn {

    @Autowired
    private DaoIn daoIn;

    public String show() {
        return daoIn.show();
    }
}
