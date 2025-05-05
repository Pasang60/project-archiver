/**
 * Author: Pasang Gelbu Sherpa
 * User:VICTUS
 * Date:5/5/2026
 * Time:7:17 PM
 */

package com.pasang.projectarchiver.otp.repository;



import com.pasang.projectarchiver.otp.entity.OTP;
import com.pasang.projectarchiver.otp.entity.OTPPurpose;
import com.pasang.projectarchiver.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByUserAndOtpValue(Users user, String otp);

    Optional<OTP> findByOtpValueAndPurpose(String otp, OTPPurpose purpose);
}
