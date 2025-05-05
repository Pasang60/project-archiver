package com.pasang.projectarchiver.users.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOtpRequest {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Otp is required")
    private String otp;

}
