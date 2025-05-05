/**
 * Author: Pasang Gelbu Sherpa
 * User:VICTUS
 * Date:5/5/2026
 * Time:1:22 PM
 */

package com.pasang.projectarchiver.utils.password;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordGeneratorUtil {
    private PasswordGeneratorUtil() {}
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?/[]{}";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateStrongPassword() {

        List<Character> passwordChars = new ArrayList<>();

        // Ensuring at least one character from each category
        passwordChars.add(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        passwordChars.add(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        passwordChars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the remaining characters randomly
        for (int i = 4; i < 12; i++) {
            passwordChars.add(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle to avoid predictable patterns
        Collections.shuffle(passwordChars);

        // Convert list to string
        StringBuilder password = new StringBuilder();
        for (char ch : passwordChars) {
            password.append(ch);
        }

        return password.toString();
    }
}
