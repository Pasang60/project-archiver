package com.pasang.projectarchiver.users.service;

import com.pasang.projectarchiver.mail.MailService;
import com.pasang.projectarchiver.otp.entity.OTP;
import com.pasang.projectarchiver.otp.entity.OTPPurpose;
import com.pasang.projectarchiver.otp.service.OTPService;
import com.pasang.projectarchiver.role.entity.Role;
import com.pasang.projectarchiver.role.repository.RoleRepository;
import com.pasang.projectarchiver.users.dto.request.PasswordRequest;
import com.pasang.projectarchiver.users.dto.request.UpdateUserRequest;
import com.pasang.projectarchiver.users.dto.request.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.request.ValidateOtpRequest;
import com.pasang.projectarchiver.users.dto.response.UsersResponse;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.message.UserExceptionMessage;
import com.pasang.projectarchiver.users.message.UserLogMessage;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import com.pasang.projectarchiver.utils.file.FileHandlerUtil;
import com.pasang.projectarchiver.utils.logged_in_user.LoggedInUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService{
    private final UsersRepository usersRepository;
    private final FileHandlerUtil fileHandlerUtil;
    private final RoleRepository roleRepository;
    private final OTPService otpService;
    private final MailService mailService;
    private final LoggedInUser loggedInUser;
    @Override
    public UsersResponse registerUser(UsersRegistrationRequest usersRegistrationRequest) {
        log.info("Registering user: {}", usersRegistrationRequest);
        Users user = new Users();
        user.setFirstName(usersRegistrationRequest.getFirstName());
        user.setLastName(usersRegistrationRequest.getLastName());
        user.setEmail(usersRegistrationRequest.getEmail());
        user.setPhone(usersRegistrationRequest.getPhone());
        user.setAddress(usersRegistrationRequest.getAddress());
        user.setProfilePic(fileHandlerUtil.saveFile(usersRegistrationRequest.getProfilePic(), "profileImages").getFileDownloadUri());
        user.setPassword(new BCryptPasswordEncoder().encode(usersRegistrationRequest.getPassword()));
        user.setStatus(true);

        // Set role
        user.setRole(
                List.of(
                        roleRepository.findByName(Role.ROLE_USER)
                                .orElseThrow(() -> new EntityNotFoundException(UserExceptionMessage.ROLE_NOT_FOUND + Role.ROLE_USER))
                )
        );
        usersRepository.save(user);
        log.info("User registered: {}", user);

        OTP otp = otpService.saveOTP(user, OTPPurpose.REGISTER);
        String fullName = user.getFirstName()+" "+user.getLastName();
        mailService.sendOtpEmail(user.getEmail(), fullName, otp.getOtpValue(), otp.getExpiryTime());

        log.info(UserLogMessage.USER_REQUEST_MAIL_SENT , user.getEmail());



        return new UsersResponse(user);

    }

    @Override
    public String validateOtp(ValidateOtpRequest otpRequest) {
        log.info("Validating OTP for user" );
        Users user = usersRepository.findByEmail(otpRequest.getEmail()).orElseThrow(
                () -> new IllegalArgumentException(UserExceptionMessage.USER_NOT_FOUND + otpRequest.getEmail())
        );
        otpService.validateOTP(user, otpRequest.getOtp());
            user.setIsVerified(true);
            usersRepository.save(user);
            return "User verified successfully";
    }

    @Override
    public String setPassword(PasswordRequest passwordRequest) {
        log.info("Setting password for user" );
        Users user = usersRepository.findByEmail(passwordRequest.getEmail()).orElseThrow(
                () -> new IllegalArgumentException(UserExceptionMessage.USER_NOT_FOUND + passwordRequest.getEmail())
        );
        if (Boolean.TRUE.equals(user.getIsVerified())) {
            if (passwordRequest.getPassword().equals(passwordRequest.getConfirmPassword())) {
                user.setPassword(new BCryptPasswordEncoder().encode(passwordRequest.getPassword()));
            } else {
                log.warn("Password didn't match");
                throw new IllegalArgumentException("Password doesn't match.");
            }
            usersRepository.save(user);
            return "Password set successfully";
        } else {
            throw new IllegalArgumentException(UserExceptionMessage.USER_NOT_VERIFIED + passwordRequest.getEmail());
        }
    }

    @Override
    public UsersResponse updateUser(UpdateUserRequest updateUserRequest) {
        log.info("Updating user: {}", updateUserRequest);
        Long userId = loggedInUser.getLoggedInUser().getId();
        Users user = usersRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(UserExceptionMessage.USER_NOT_FOUND + userId)
        );
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setEmail(updateUserRequest.getEmail());
        user.setPhone(updateUserRequest.getPhone());
        user.setAddress(updateUserRequest.getAddress());
        user.setProfilePic(fileHandlerUtil.saveFile(updateUserRequest.getProfilePic(), "profileImages").getFileDownloadUri());
        usersRepository.save(user);
        log.info("User updated: {}", user);
        return new UsersResponse(user);
    }

    @Override
    public UsersResponse getUserById() {
    log.info("Fetching user by ID");
    Long userId = loggedInUser.getLoggedInUser().getId();
    Users user = usersRepository.findById(userId).orElseThrow(
            () -> new EntityNotFoundException(UserExceptionMessage.USER_NOT_FOUND + userId)
    );
    log.info("User fetched: {}", user);
    if (user != null) {
        return new UsersResponse(user);
    }
    else {
        log.warn("User not found with ID: {}", userId);
        throw new EntityNotFoundException(UserExceptionMessage.USER_NOT_FOUND + userId);
    }
    }

    @Override
    public Long getVerifiedUsersCount() {
        log.info("Fetching count of verified users");
        Long count = usersRepository.countUsersByIsVerified(true);
        log.info("Count of verified users: {}", count);
        return count;
    }
}
