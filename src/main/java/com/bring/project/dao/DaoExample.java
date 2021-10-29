package com.bring.project.dao;

import com.bring.project.annotation.Dao;

@Dao
public class DaoExample implements DaoIn{

    public DaoExample() {
    }

    public String show() {
        System.out.println("In DAO!!!");
        return "DAO";
    }
}
