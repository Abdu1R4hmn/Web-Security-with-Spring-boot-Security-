package com.example.demo.auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginResponseDto {
    private final String accessToken;

    public LoginResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
