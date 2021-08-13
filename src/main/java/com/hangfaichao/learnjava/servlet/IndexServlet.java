package com.hangfaichao.learnjava.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/***
 * @author zhh
 * @date 2021/08/11
 */
//@WebServlet(urlPatterns = "/")
public class IndexServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = (String) request.getSession().getAttribute("user");

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Powered-By", "JavaEE Servlet");
        PrintWriter pw = response.getWriter();
        pw.write("<h1>Welcome, " + (username != null ? username : "Guest") + "</h1>");
        if (username == null) {
            // 未登录，显示登录链接:
            pw.write("<p><a href=\"/signin\">Sign In</a></p>");
        } else {
            // 已登录，显示登出链接:
            pw.write("<p><a href=\"/signout\">Sign Out</a></p>");
        }
        pw.flush();
    }
}
