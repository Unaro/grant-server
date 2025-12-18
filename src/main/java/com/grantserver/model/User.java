package com.grantserver.model;

public class User {
    public Long id;
    public String login;
    public String password;

    public User() {}

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
