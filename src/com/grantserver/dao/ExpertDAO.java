package com.grantserver.dao;

import com.grantserver.model.Expert;
import java.util.List;

public interface ExpertDAO {
    Expert save(Expert expert);
    Expert findById(Long id);
    Expert findByLogin(String login);
    List<Expert> findAll();
    boolean delete(Long id);
}