package com.hangfaichao.learnjava.controller;

import com.hangfaichao.learnjava.bean.SignInBean;
import com.hangfaichao.learnjava.bean.User;
import com.hangfaichao.learnjava.framework.GetMapping;
import com.hangfaichao.learnjava.framework.ModelAndView;
import com.hangfaichao.learnjava.framework.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * @author zhh
 * @date 2021/08/11
 */
public class UserController {

    List<User> userList = new LinkedList<User>(){{
        add(new User(1, "zhh", "zhao", ""));
        add(new User(2, "hry", "hu", ""));
    }};
    Map<String, User> database = userList.stream().collect(Collectors.toMap(User::getName, item -> item));

    @GetMapping("/signin")
    public ModelAndView signin() {
        ModelAndView mv = new ModelAndView("/signin.html");
        return mv;
    }

    @PostMapping("/signin")
    public ModelAndView doSignin(HttpSession session, HttpServletResponse response, SignInBean bean) throws IOException {
        User user = database.get(bean.getUsername());
        if (user != null && user.getPassword().equals(bean.getPassword())) {
            session.setAttribute("user", user);
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"result\":true}");
            pw.flush();
        } else {
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\":\"Bad email or password\"}");
            pw.flush();
        }
        return null;
    }

    @GetMapping("/signout")
    public ModelAndView signout(HttpSession session) {
        session.removeAttribute("user");
        return new ModelAndView("redirect:/signin");
    }

    @GetMapping("/user/profile")
    public ModelAndView profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/signin");
        }
        return new ModelAndView("/profile.html", "user", user);
    }
}
