package com.hangfaichao.learnjava.bean;

/***
 * @author zhh
 * @date 2021/08/11
 */
public class User {
    private long id;
    private String name;
    private String password;
    private String description;

    public User() {
    }

    public User(long id, String name, String password, String description) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
