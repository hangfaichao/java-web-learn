package com.hangfaichao.learnjava.bean;

/***
 * @author zhh
 * @date 2021/08/11
 */
public class School {
    private String name;
    private String address;

    public School(String name, String address) {
        this.name = name;
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
}
