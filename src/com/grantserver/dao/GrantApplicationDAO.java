package com.grantserver.dao;

import com.grantserver.model.GrantApplication;
import java.util.List;

public interface GrantApplicationDAO {
    GrantApplication save(GrantApplication application);
    GrantApplication findById(Long id);
    List<GrantApplication> findAll();
    List<GrantApplication> findAllByOwnerId(Long ownerId);
}