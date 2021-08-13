<%--
  Created by IntelliJ IDEA.
  User: zhh
  Date: 2021/8/11
  Time: 9:28 下午
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.hangfaichao.learnjava.bean.*" contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) request.getAttribute("user");
%>
<html>
<head>
    <title>Hello World - JSP</title>
</head>
<body>
<h1>Hello <%= user.getName() %>!</h1>
<p>School Name:
    <span style="color:red">
        <%= user.getSchool().getName() %>
    </span>
</p>
<p>School Address:
    <span style="color:red">
        <%= user.getSchool().getAddress() %>
    </span>
</p>
</body>
</html>
