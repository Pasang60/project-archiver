/**
 * Author: Pasang Gelbu Sherpa
 * User:VICTUS
 * Date:5/5/2026
 * Time:12:45 PM
 */

package com.pasang.projectarchiver.utils.otp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "otp")
public class OtpProperties {
    private int length;
    private boolean alphanumeric;
    private int expiryInSeconds;
}
