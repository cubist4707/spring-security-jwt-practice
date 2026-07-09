package org.example.signup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank()
    private String username;

    @NotBlank()
    private String password;

    @NotBlank()
    private String passwordConfirm;

    @NotBlank()
    private String email;
}
