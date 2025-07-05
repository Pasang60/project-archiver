package com.pasang.projectarchiver.users.controller;

/*
    * @author Pasang Gelbu Sherpa *
 */


import com.pasang.projectarchiver.constant.SystemMessage;
import com.pasang.projectarchiver.global.BaseController;
import com.pasang.projectarchiver.global.GlobalApiResponse;
import com.pasang.projectarchiver.users.dto.request.PasswordRequest;
import com.pasang.projectarchiver.users.dto.request.UpdateUserRequest;
import com.pasang.projectarchiver.users.dto.request.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.request.ValidateOtpRequest;
import com.pasang.projectarchiver.users.message.UserSwaggerDocumentationMessage;
import com.pasang.projectarchiver.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController extends BaseController {
    private final UsersService usersService;

    @Operation(
            summary = UserSwaggerDocumentationMessage.ADD_USER_SUMMARY,
            description = UserSwaggerDocumentationMessage.ADD_USER_DESCRIPTION
    )
    @PostMapping("/register")
    public ResponseEntity<GlobalApiResponse> registerUser(@ModelAttribute UsersRegistrationRequest usersRegistrationRequest) {
        return successResponse(usersService.registerUser(usersRegistrationRequest), SystemMessage.USER_REGISTERED);
    }

    @Operation(
            summary = UserSwaggerDocumentationMessage.VALIDATE_OTP_SUMMARY,
            description = UserSwaggerDocumentationMessage.VALIDATE_OTP_DESCRIPTION
    )
    @PostMapping("/validate")
    public ResponseEntity<GlobalApiResponse> validateOtp(@RequestBody ValidateOtpRequest validateOtpRequest) {
        return successResponse(usersService.validateOtp(validateOtpRequest), SystemMessage.OTP_VERIFIED);
    }

    @PostMapping("/set-password")
    @Operation(
            summary = UserSwaggerDocumentationMessage.SET_PASSWORD_SUMMARY,
            description = UserSwaggerDocumentationMessage.SET_PASSWORD_DESCRIPTION
    )
    public ResponseEntity<GlobalApiResponse> setPassword(@RequestBody PasswordRequest passwordRequest) {
        return successResponse(usersService.setPassword(passwordRequest), SystemMessage.PASSWORD_SET);
    }

    @PutMapping("/update")
    @Operation(
            summary = UserSwaggerDocumentationMessage.UPDATE_USER_SUMMARY,
            description = UserSwaggerDocumentationMessage.UPDATE_USER_DESCRIPTION
    )
    public ResponseEntity<GlobalApiResponse> updateUser(@ModelAttribute UpdateUserRequest updateUserRequest) {
        return successResponse(usersService.updateUser(updateUserRequest), SystemMessage.USER_UPDATED);
    }

    @GetMapping("/getById")
    @Operation(
            summary = UserSwaggerDocumentationMessage.GET_USER_BY_ID_SUMMARY,
            description = UserSwaggerDocumentationMessage.GET_USER_BY_ID_DESCRIPTION
    )
    public ResponseEntity<GlobalApiResponse> getUserById() {
        return successResponse(usersService.getUserById(), SystemMessage.USER_FETCHED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get the count of Archived Files",
    description = "Fetch the count of verified users in the system")
    @GetMapping("/getVerifiedUsersCount")
    public ResponseEntity<GlobalApiResponse> getVerifiedUsersCount() {
        return successResponse(usersService.getVerifiedUsersCount(), "Count of verified users retrieved successfully");
    }

}
