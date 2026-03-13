package com.grantserver.service;

import com.grantserver.dto.response.GrantResultDTO;

public interface GrantFundService {
    GrantResultDTO calculate(Integer fund, Double threshold);
}