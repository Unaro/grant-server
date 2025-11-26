package com.grantserver.model;

import java.util.List;

public class Expert {
    public Long id;
    public String firstName;
    public String lastName;
    public List<String> fields; // Компетенции
    public String login;
    public String password;

    public Expert() {}
}