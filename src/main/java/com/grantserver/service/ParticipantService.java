package com.grantserver.service;

import com.grantserver.model.Participant;

public interface ParticipantService {
    Participant register(Participant participant);
    
    String login(String login, String password);
}