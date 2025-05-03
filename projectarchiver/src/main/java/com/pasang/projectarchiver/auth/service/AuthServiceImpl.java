package com.pasang.projectarchiver.auth.service;
/*
 * @author Pasang Gelbu Sherpa *
 */

import com.pasang.projectarchiver.auth.dto.request.AuthRequest;
import com.pasang.projectarchiver.auth.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse login(AuthRequest authRequest) {
        log.info("Login request received");
        return null;
    }
}
