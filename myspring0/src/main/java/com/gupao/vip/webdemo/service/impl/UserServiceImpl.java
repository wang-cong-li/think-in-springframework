package com.gupao.vip.webdemo.service.impl;

import com.gupao.vip.myspringframework.v0.annotation.Service;
import com.gupao.vip.webdemo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    public String getUserName() {
        return "haha";
    }
}
