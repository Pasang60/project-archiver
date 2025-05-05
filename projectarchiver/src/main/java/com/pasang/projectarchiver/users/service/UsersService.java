package com.pasang.projectarchiver.users.service;

import com.pasang.projectarchiver.users.dto.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.UsersResponse;

public interface UsersService {
    UsersResponse registerUser(UsersRegistrationRequest usersRegistrationRequest);
}
