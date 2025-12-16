package com.grantserver.dto.response;

import com.grantserver.model.Expert;
import java.util.List;

public class ExpertDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public List<String> fields;
    public String login;

    public ExpertDTO() {}

    public ExpertDTO(Expert expert) {
        this.id = expert.id;
        this.firstName = expert.firstName;
        this.lastName = expert.lastName;
        this.fields = expert.fields;
        this.login = expert.login;
    }
}