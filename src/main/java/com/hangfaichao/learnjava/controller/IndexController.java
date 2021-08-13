package com.hangfaichao.learnjava.controller;

import com.hangfaichao.learnjava.bean.User;
import com.hangfaichao.learnjava.framework.GetMapping;
import com.hangfaichao.learnjava.framework.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * @author zhh
 * @date 2021/08/13
 */
public class IndexController {

    @GetMapping("/")
    public ModelAndView index(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return new ModelAndView("/index.html", "user", user);
    }

    @GetMapping("/hello")
    public ModelAndView hello(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return new ModelAndView( "/hello.html", "name", user.getName());
    }
}
