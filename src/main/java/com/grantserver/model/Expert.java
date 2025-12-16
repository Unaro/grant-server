package com.grantserver.model;

import java.util.List;

public class Expert extends User {
    public String firstName;
    public String lastName;
    public List<String> fields; // Компетенции

    public Expert() {}
}