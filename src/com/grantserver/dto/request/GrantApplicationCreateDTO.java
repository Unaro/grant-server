package com.grantserver.dto.request;

import java.util.List;

public class GrantApplicationCreateDTO {
    public String title;
    public String description;
    public List<String> fields;
    public Integer requestedSum;

    public GrantApplicationCreateDTO() {}
}