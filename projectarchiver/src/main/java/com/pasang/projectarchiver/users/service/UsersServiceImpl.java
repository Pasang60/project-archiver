package com.pasang.projectarchiver.users.service;

import com.pasang.projectarchiver.mail.MailService;
import com.pasang.projectarchiver.otp.entity.OTP;
import com.pasang.projectarchiver.otp.entity.OTPPurpose;
import com.pasang.projectarchiver.otp.service.OTPService;
import com.pasang.projectarchiver.role.entity.Role;
import com.pasang.projectarchiver.role.repository.RoleRepository;
import com.pasang.projectarchiver.users.dto.request.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.response.UsersResponse;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.message.UserExceptionMessage;
import com.pasang.projectarchiver.users.message.UserLogMessage;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import com.pasang.projectarchiver.utils.file.FileHandlerUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Override
    public UsersResponse registerUser(UsersRegistrationRequest usersRegistrationRequest) {
        log.info("Registering user: {}", usersRegistrationRequest);
        Users user = new Users();
        user.setFullName(usersRegistrationRequest.getFullName());
        user.setEmail(usersRegistrationRequest.getEmail());
        user.setPhone(usersRegistrationRequest.getPhone());
        user.setAddress(usersRegistrationRequest.getAddress());
        user.setProfilePic(fileHandlerUtil.saveFile(usersRegistrationRequest.getProfilePic(), "profileImages").getFileDownloadUri());
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
        mailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp.getOtpValue(), otp.getExpiryTime());

        log.info(UserLogMessage.USER_REQUEST_MAIL_SENT , user.getEmail());



        return new UsersResponse(user);

    }
}
