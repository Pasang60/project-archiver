/**
 * Author: Pasang Gelbu Sherpa
 * User:VICTUS
 * Date:5/5/2026
 * Time:7:17 PM
 */

package com.pasang.projectarchiver.otp.entity;


import com.pasang.projectarchiver.users.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Users user;

    @Column(unique = true, nullable = false)
    private String otpValue;

    private LocalDateTime expiryTime;

    private Boolean isUsed;

    @Enumerated(EnumType.STRING)
    private OTPPurpose purpose;

    @PrePersist
    public void prePersist() {
        this.isUsed = false;
    }
}
