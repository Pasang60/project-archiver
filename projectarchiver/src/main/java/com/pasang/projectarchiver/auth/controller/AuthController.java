/*
 * @author Pasang Gelbu Sherpa *
 */
package com.pasang.projectarchiver.auth.controller;

import com.pasang.projectarchiver.auth.dto.request.AuthRequest;
import com.pasang.projectarchiver.auth.service.AuthService;
import com.pasang.projectarchiver.constant.SystemMessage;
import com.pasang.projectarchiver.global.BaseController;
import com.pasang.projectarchiver.global.GlobalApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GlobalApiResponse> login(@RequestBody AuthRequest authRequest) {
        return successResponse(authService.login(authRequest), SystemMessage.USER_LOGGED_IN_MSG);
    }
}
