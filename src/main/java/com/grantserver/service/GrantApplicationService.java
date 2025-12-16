package com.grantserver.service;

import com.grantserver.dto.request.GrantApplicationCreateDTO;
import com.grantserver.dto.response.GrantApplicationDTO;
import java.util.List;

public interface GrantApplicationService {
    GrantApplicationDTO create(GrantApplicationCreateDTO dto, Long ownerId);
    List<GrantApplicationDTO> getAll();
    List<GrantApplicationDTO> getByOwner(Long ownerId);
}