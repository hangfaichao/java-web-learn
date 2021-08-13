package com.hangfaichao.learnjava.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/***
 * @author zhh
 * @date 2021/08/11
 */
//@WebServlet(urlPatterns = "/signin")
public class SignInServlet extends HttpServlet {

    private Map<String, String> users = new HashMap<String, String>(){{put("zhh", "zhao"); put("hry", "hu");}};

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.write("<h1>Sign In</h1>");
        pw.write("<form action=\"/signin\" method=\"post\">");
        pw.write("<p>Username: <input name=\"username\"></p>");
        pw.write("<p>Password: <input name=\"password\" type=\"password\"></p>");
        pw.write("<p><button type=\"submit\">Sign In</button> <a href=\"/\">Cancel</a></p>");
        pw.write("</form>");
        pw.flush();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String expectedPassword = users.get(username);
        if (expectedPassword != null && expectedPassword.equals(password)) {
            request.getSession().setAttribute("user", username);
            response.sendRedirect("/");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}