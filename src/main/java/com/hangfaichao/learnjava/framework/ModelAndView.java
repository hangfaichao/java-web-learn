package com.hangfaichao.learnjava.framework;

import java.util.HashMap;
import java.util.Map;

/***
 * @author zhh
 * @date 2021/08/11
 */
public class ModelAndView {
    Map<String, Object> model;
    String view;

    public ModelAndView(String view) {
        this.view = view;
    }

    public ModelAndView(String view, Map<String, Object> model) {
        this.model = model;
        this.view = view;
    }

    public ModelAndView(String view, String key, Object value) {
        this.view = view;
        this.model = new HashMap<String, Object>(){{put(key, value);}};
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
