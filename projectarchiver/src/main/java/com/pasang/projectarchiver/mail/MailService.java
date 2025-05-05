package com.pasang.projectarchiver.mail;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

public interface MailService {


    @Async
    void sendOtpEmail(String to, String name, String otp, LocalDateTime expiry);

}
