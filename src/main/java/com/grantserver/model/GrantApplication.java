package com.grantserver.model;

import java.util.List;

public class GrantApplication {
    public Long id;
    public String title;
    public String description;
    public List<String> fields;
    public Integer requestedSum;
    public GrantApplicationStatus status;

    public GrantApplication() {}
}