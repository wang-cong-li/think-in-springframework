package com.gupao.vip.webdemo.controller;

import com.gupao.vip.myspringframework.v0.annotation.Autowired;
import com.gupao.vip.myspringframework.v0.annotation.Controller;
import com.gupao.vip.myspringframework.v0.annotation.RequestMapping;
import com.gupao.vip.myspringframework.v0.annotation.RequestParam;
import com.gupao.vip.webdemo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("userController")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/getUserInfo")
    public Object getUserInfo(HttpServletRequest request, HttpServletResponse response,@RequestParam("userId") String userId) {
        return userService.getUserName();
    }

}
