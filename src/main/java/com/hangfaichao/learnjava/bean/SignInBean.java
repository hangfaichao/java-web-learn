package com.hangfaichao.learnjava.bean;

/**
 * @author zhh
 * @date 2021/08/12
 */
public class SignInBean {
    private String username;
    private String password;

    public SignInBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SignInBean() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
