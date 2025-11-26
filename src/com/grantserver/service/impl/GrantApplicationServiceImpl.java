package com.grantserver.service.impl;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dto.request.GrantApplicationCreateDTO;
import com.grantserver.dto.response.GrantApplicationDTO;
import com.grantserver.model.GrantApplication;
import com.grantserver.model.GrantApplicationStatus;
import com.grantserver.service.GrantApplicationService;

import java.util.List;
import java.util.stream.Collectors;

public class GrantApplicationServiceImpl implements GrantApplicationService {

    private final GrantApplicationDAO grantApplicationDAO;

    public GrantApplicationServiceImpl() {
        this.grantApplicationDAO = ServiceRegistry.getInstance().get(GrantApplicationDAO.class);
    }

    @Override
    public GrantApplicationDTO create(GrantApplicationCreateDTO dto, Long ownerId) {
        GrantApplication app = new GrantApplication();
        app.title = dto.title;
        app.description = dto.description;
        app.fields = dto.fields;
        app.requestedSum = dto.requestedSum;
        app.ownerId = ownerId;
        app.status = GrantApplicationStatus.ACTIVE; // По умолчанию активна

        GrantApplication saved = grantApplicationDAO.save(app);
        
        return new GrantApplicationDTO(saved);
    }

    @Override
    public List<GrantApplicationDTO> getAll() {
        return grantApplicationDAO.findAll().stream()
                .map(GrantApplicationDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<GrantApplicationDTO> getByOwner(Long ownerId) {
        return grantApplicationDAO.findAllByOwnerId(ownerId).stream()
                .map(GrantApplicationDTO::new)
                .collect(Collectors.toList());
    }
}