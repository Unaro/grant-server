package com.grantserver.dto.response;

import com.grantserver.model.Manager;
import com.grantserver.model.Participant;

public class ParticipantDTO {
    public Long id;
    public String firmName;
    public Manager manager;
    public String login;

    public ParticipantDTO() {}

    // Удобный конструктор для конвертации из Entity
    public ParticipantDTO(Participant participant) {
        this.id = participant.id;
        this.firmName = participant.firmName;
        this.manager = participant.manager;
        this.login = participant.login;
    }
}