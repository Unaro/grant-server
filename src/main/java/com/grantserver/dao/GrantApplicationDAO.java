package com.grantserver.dao;

import java.util.List;

import com.grantserver.model.GrantApplication;

public interface GrantApplicationDAO {
    GrantApplication findById(Long id);
    List<GrantApplication> findAll();
    Long generateId();
    List<GrantApplication> findAllByOwnerId(Long ownerId);
}