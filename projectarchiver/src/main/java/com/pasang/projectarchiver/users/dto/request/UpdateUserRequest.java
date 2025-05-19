package com.pasang.projectarchiver.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotEmpty(message = "User fullName must not be empty")
    private String fullName;

    @NotEmpty(message = "User email must not be empty") // Neither null nor 0 size
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Phone number must not be empty")
    private String phone;

    private MultipartFile profilePic;

    @NotEmpty(message = "Address must not be empty")
    private String address;
}
