package com.grantserver.service;

import java.util.List;

import com.grantserver.model.GrantApplication;

public interface GrantApplicationService {
    GrantApplication create(GrantApplication app, Long ownerId);
    List<GrantApplication> getAll();
    List<GrantApplication> getByOwner(Long ownerId);
}