package com.pasang.projectarchiver.users.service;

import com.pasang.projectarchiver.users.dto.request.PasswordRequest;
import com.pasang.projectarchiver.users.dto.request.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.request.ValidateOtpRequest;
import com.pasang.projectarchiver.users.dto.response.UsersResponse;

public interface UsersService {
    UsersResponse registerUser(UsersRegistrationRequest usersRegistrationRequest);

    String validateOtp(ValidateOtpRequest validateOtpRequest);

    String setPassword(PasswordRequest passwordRequest);
}
