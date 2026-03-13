package com.grantserver.dto.response;

import java.util.List;

import com.grantserver.model.GrantApplication;

public class GrantApplicationDTO {
    public Long id;
    public String title;
    public String description;
    public List<String> fields;
    public Integer requestedSum;
    public Long ownerId;
    public String status;

    public GrantApplicationDTO() {}

    // Конструктор для конвертации из Entity
    public GrantApplicationDTO(GrantApplication app) {
        this.id = app.id;
        this.title = app.title;
        this.description = app.description;
        this.fields = app.fields;
        this.requestedSum = app.requestedSum;
        this.ownerId = app.owner.id;
        this.status = app.status != null ? app.status.name() : null;
    }
}