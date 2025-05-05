package com.pasang.projectarchiver.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    private Long userId;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private String role;
    private String status;
}
