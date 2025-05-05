/**
 * Author: Pasang Gelbu Sherpa
 * User:VICTUS
 * Date:5/5/2026
 * Time:7:17 PM
 */

package com.pasang.projectarchiver.otp.service;


import com.pasang.projectarchiver.otp.entity.OTP;
import com.pasang.projectarchiver.otp.entity.OTPPurpose;
import com.pasang.projectarchiver.otp.repository.OTPRepository;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.utils.otp.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OTPServiceImplementation implements OTPService {

    private final OTPRepository otpRepository;
    private final OtpUtil otpUtil;

    @Value("${otp.expiryInSeconds}")
    private long expiryTime;

    @Override
    public OTP saveOTP(Users user, OTPPurpose purpose) {
        OTP otp = OTP.builder()
                .user(user)
                .otpValue(otpUtil.generateOtp(user.getEmail()))
                .expiryTime(LocalDateTime.now().plusSeconds(expiryTime))
                .purpose(purpose)
                .build();
        return otpRepository.save(otp);
    }

    @Override
    public void validateOTP(Users user, String otp) {
        OTP otpEntity = otpRepository.findByUserAndOtpValue(user, otp)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));
        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }
        otpEntity.setIsUsed(true);
        otpRepository.save(otpEntity);
    }

    @Override
    public OTP getOTP(String otp, OTPPurpose purpose) {
        OTP userOTP = otpRepository.findByOtpValueAndPurpose(otp, purpose)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));
        if (userOTP.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }
        return userOTP;
    }

}
