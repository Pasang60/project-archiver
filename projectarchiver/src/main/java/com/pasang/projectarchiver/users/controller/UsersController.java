package com.pasang.projectarchiver.users.controller;

/*
    * @author Pasang Gelbu Sherpa *
 */


import com.pasang.projectarchiver.constant.SystemMessage;
import com.pasang.projectarchiver.global.BaseController;
import com.pasang.projectarchiver.global.GlobalApiResponse;
import com.pasang.projectarchiver.users.dto.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController extends BaseController {
    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<GlobalApiResponse> registerUser(@ModelAttribute UsersRegistrationRequest usersRegistrationRequest) {
        return successResponse(usersService.registerUser(usersRegistrationRequest), SystemMessage.USER_REGISTERED);
    }
}
