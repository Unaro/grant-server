package com.grantserver.service;

import com.grantserver.dto.request.GrantFundRequestDTO;
import com.grantserver.dto.response.GrantResultDTO;

public interface GrantFundService {
    GrantResultDTO calculate(GrantFundRequestDTO requestDTO);
}