package com.grantserver.model;

import java.util.List;

public class GrantApplication {
    public Long id;
    public String title;
    public String description;
    public List<String> fields;
    public Integer requestedSum;
    public Long ownerId; // ID участника, подавшего заявку
    public GrantApplicationStatus status;

    public GrantApplication() {}
}