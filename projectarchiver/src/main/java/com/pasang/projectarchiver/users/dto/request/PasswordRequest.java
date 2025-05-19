package com.pasang.projectarchiver.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    private String email;
    private String password;
    private String confirmPassword;
}
