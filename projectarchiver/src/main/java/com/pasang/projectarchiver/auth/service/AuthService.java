package com.pasang.projectarchiver.auth.service;

import com.pasang.projectarchiver.auth.dto.request.AuthRequest;
import com.pasang.projectarchiver.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);

    void logout();
}
