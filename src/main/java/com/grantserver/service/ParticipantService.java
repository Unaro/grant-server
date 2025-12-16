package com.grantserver.service;

import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.request.ParticipantRegisterDTO;
import com.grantserver.dto.response.AuthResponseDTO;
import com.grantserver.dto.response.ParticipantDTO;

public interface ParticipantService {
    ParticipantDTO register(ParticipantRegisterDTO registerDTO);
    
    AuthResponseDTO login(AuthRequestDTO authDTO);
}