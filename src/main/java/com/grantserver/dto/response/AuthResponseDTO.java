package com.grantserver.dto.response;

public class AuthResponseDTO {
    public String token;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token) {
        this.token = token;
    }
}