package com.grantserver.dao;

import java.util.List;

public interface UserDAO<T> {
    T save(T expert);
    T findById(Long id);
    T findByLogin(String login);
    List<T> findAll();
    boolean delete(Long id);
}