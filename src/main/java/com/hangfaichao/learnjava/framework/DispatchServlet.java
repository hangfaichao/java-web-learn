package com.hangfaichao.learnjava.framework;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangfaichao.learnjava.controller.IndexController;
import com.hangfaichao.learnjava.controller.UserController;
import com.sun.deploy.security.ruleset.DRSResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/***
 * @author zhh
 * @date 2021/08/11
 */

@WebServlet(urlPatterns = "/")
public class DispatchServlet extends HttpServlet {

    private Map<String, AbstractDispatcher> getMappings = new HashMap<>();
    private Map<String, AbstractDispatcher> postMappings = new HashMap<>();
    private ViewEngine viewEngine;

    private List<Class<?>> controllers = new LinkedList<Class<?>>(){{
        add(UserController.class);
        add(IndexController.class);
    }};

    @Override
    public void init() throws ServletException {
        scanControllers();
        this.viewEngine = new ViewEngine(getServletContext());
    }

    private void scanControllers() {
        Map<String, AbstractDispatcher> resultMap = new HashMap<>();
        for (Class<?> classType: controllers) {
            try {
                Object controller = classType.getConstructor().newInstance();
                for (Method method : classType.getMethods()) {
                    if (method.getAnnotation(GetMapping.class) != null) {
                        GetMapping annotation = method.getAnnotation(GetMapping.class);
                        Parameter[] parameters = method.getParameters();
                        String[] parameterNames = new String[parameters.length];
                        Class<?>[] parameterTypes = new Class[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            parameterNames[i] = parameters[i].getName();
                            parameterTypes[i] = parameters[i].getType();
                        }
                        this.getMappings.put(annotation.value(), new GetDispatcher(controller, method, parameterNames, parameterTypes));
                    } else if (method.getAnnotation(PostMapping.class) != null) {
                        PostMapping annotation = method.getAnnotation(PostMapping.class);
                        this.postMappings.put(annotation.value(), new PostDispatcher(controller, method, method.getParameterTypes()));
                    }
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse rsp)
            throws IOException {
        process(req, rsp, getMappings);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        process(request, response, postMappings);
    }

    public void process(HttpServletRequest req, HttpServletResponse rsp, Map<String, AbstractDispatcher> dispatcherMap) throws IOException {
        rsp.setContentType("text/html");
        rsp.setCharacterEncoding("UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        AbstractDispatcher dispatcher = dispatcherMap.get(path);
        if (dispatcher == null) {
            rsp.sendError(404);
            return;
        }
        ModelAndView mv;
        try {
            mv = dispatcher.invoke(req, rsp);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        // 允许返回null:
        if (mv == null) {
            return;
        }
        // 允许返回`redirect:`开头的view表示重定向:
        if (mv.view.startsWith("redirect:")) {
            rsp.sendRedirect(mv.view.substring(9));
            return;
        }
        // 将模板引擎渲染的内容写入响应:
        PrintWriter pw = rsp.getWriter();
        this.viewEngine.render(mv, pw);
        pw.flush();
    }

}

abstract class AbstractDispatcher {
    /**
     * 解析参数并调用相应controller处理请求
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public abstract ModelAndView invoke(HttpServletRequest request, HttpServletResponse response)
            throws IOException, InvocationTargetException, IllegalAccessException;
}


class GetDispatcher extends AbstractDispatcher {
    /**
     * Controller实例
     */
    Object instance;
    /**
     * Controller方法
     */
    Method method;
    /**
     * 方法参数名称
     */
    String[] parameterNames;
    /**
     * 方法参数类型
     */
    Class<?>[] parameterClasses;

    public GetDispatcher(Object instance, Method method, String[] parameterNames, Class<?>[] parameterClasses) {
        this.instance = instance;
        this.method = method;
        this.parameterNames = parameterNames;
        this.parameterClasses = parameterClasses;
    }

    @Override
    public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response)
            throws InvocationTargetException, IllegalAccessException {
        Object[] arguments = new Object[parameterClasses.length];
        for (int i = 0; i < parameterClasses.length; i++) {
            String parameterName = parameterNames[i];
            Class<?> parameterClass = parameterClasses[i];
            if (parameterClass == HttpServletRequest.class) {
                arguments[i] = request;
            } else if (parameterClass == HttpServletResponse.class) {
                arguments[i] = response;
            } else if (parameterClass == HttpSession.class) {
                arguments[i] = request.getSession();
            } else if (parameterClass == int.class) {
                arguments[i] = Integer.valueOf(getOrDefault(request, parameterName, "0"));
            } else if (parameterClass == long.class) {
                arguments[i] = Long.valueOf(getOrDefault(request, parameterName, "0"));
            } else if (parameterClass == boolean.class) {
                arguments[i] = Boolean.valueOf(getOrDefault(request, parameterName, "false"));
            } else if (parameterClass == String.class) {
                arguments[i] = getOrDefault(request, parameterName, "");
            } else {
                throw new RuntimeException("Missing handler for type: " + parameterClass);
            }
        }
        return (ModelAndView) this.method.invoke(this.instance, arguments);
    }

    private String getOrDefault(HttpServletRequest request, String parameterName, String defaultObject) {
        String parameterValue = request.getParameter(parameterName);
        return parameterValue != null ? parameterValue : defaultObject;
    }
}

class PostDispatcher extends AbstractDispatcher {
    /**
     * Controller实例
     */
    Object instance;
    /**
     * Controller方法
     */
    Method method;
    /**
     * 方法参数类型
     */
    Class<?>[] parameterClasses;
    /**
     * json映射
     */
    ObjectMapper objectMapper;

    public PostDispatcher(Object instance, Method method, Class<?>[] parameterClasses) {
        this.instance = instance;
        this.method = method;
        this.parameterClasses = parameterClasses;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response)
            throws IOException, InvocationTargetException, IllegalAccessException {
        Object[] arguments = new Object[parameterClasses.length];
        for (int i = 0; i < parameterClasses.length; i++) {
            if (parameterClasses[i] == HttpServletRequest.class) {
                arguments[i] = request;
            } else if (parameterClasses[i] == HttpServletResponse.class) {
                arguments[i] = response;
            } else if (parameterClasses[i] == HttpSession.class) {
                arguments[i] = request.getSession();
            } else {
                BufferedReader reader = request.getReader();
                arguments[i] = this.objectMapper.readValue(reader, parameterClasses[i]);
            }
        }
        return (ModelAndView) this.method.invoke(this.instance, arguments);
    }
}