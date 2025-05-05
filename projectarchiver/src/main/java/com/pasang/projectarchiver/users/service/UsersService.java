package com.pasang.projectarchiver.users.service;

import com.pasang.projectarchiver.users.dto.request.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.response.UsersResponse;

public interface UsersService {
    UsersResponse registerUser(UsersRegistrationRequest usersRegistrationRequest);
}
