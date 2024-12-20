package com.epam.module4.domain;

public enum Role {

    ADMIN, //read all, write all, grant auth
    OBSERVER, //read all, write self
    USER; //read self, write self
}
