package com.grantserver.service;

import com.grantserver.model.Expert;

public interface ExpertService {
    Expert register(Expert expert);
    
    String login(String login, String password);
    
    Expert getById(Long id);
}