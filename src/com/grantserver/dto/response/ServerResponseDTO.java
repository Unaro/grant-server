package com.grantserver.dto.response;

public class ServerResponseDTO {
    public int responseCode;
    public Object responseData; // Может быть DTO, списком или строкой ошибки

    public ServerResponseDTO() {}

    public ServerResponseDTO(int responseCode, Object responseData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
    }
}