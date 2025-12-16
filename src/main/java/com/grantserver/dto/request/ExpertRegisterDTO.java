package com.grantserver.dto.request;

import java.util.List;

public class ExpertRegisterDTO {
    public String firstName;
    public String lastName;
    public List<String> fields;
    public String login;
    public String password;

    public ExpertRegisterDTO() {}
}