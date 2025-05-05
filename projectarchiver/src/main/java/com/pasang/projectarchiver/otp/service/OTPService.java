package com.pasang.projectarchiver.otp.service;


import com.pasang.projectarchiver.otp.entity.OTP;
import com.pasang.projectarchiver.otp.entity.OTPPurpose;
import com.pasang.projectarchiver.users.entity.Users;

public interface OTPService {
    OTP saveOTP(Users user, OTPPurpose purpose);

    void validateOTP(Users user, String otp);

    OTP getOTP(String otp, OTPPurpose purpose);
}
