package com.grantserver.dto.response;

import com.grantserver.model.GrantApplication;
import java.util.List;

public class GrantApplicationDTO {
    public Long id;
    public String title;
    public String description;
    public List<String> fields;
    public Integer requestedSum;
    public Long ownerId;
    public String status; // String проще для сериализации

    public GrantApplicationDTO() {}

    // Конструктор для конвертации из Entity
    public GrantApplicationDTO(GrantApplication app) {
        this.id = app.id;
        this.title = app.title;
        this.description = app.description;
        this.fields = app.fields;
        this.requestedSum = app.requestedSum;
        this.ownerId = app.ownerId;
        this.status = app.status != null ? app.status.name() : null;
    }
}