package com.grantserver.service;

import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.request.ExpertRegisterDTO;
import com.grantserver.dto.response.AuthResponseDTO;
import com.grantserver.dto.response.ExpertDTO;

public interface ExpertService {
    ExpertDTO register(ExpertRegisterDTO registerDTO);
    AuthResponseDTO login(AuthRequestDTO authDTO);
    ExpertDTO getById(Long id);
}