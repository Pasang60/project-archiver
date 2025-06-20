package com.pasang.projectarchiver.auth.dto.response;

import com.pasang.projectarchiver.users.dto.response.RoleResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserResponse {
    private String firstName;
    private String lastName;
    private RoleResponse role;
}
