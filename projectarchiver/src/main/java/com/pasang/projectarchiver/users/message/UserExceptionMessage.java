package com.pasang.projectarchiver.users.message;

public class UserExceptionMessage {
    public static final String INVALID_PHONE_NUMBER = "Phone number is not valid";

    private UserExceptionMessage() {}
    public static final String USER_NOT_FOUND = "User not found. ";
    public static final String INVALID_PASSWORD = "Password is not valid";
    public static final String INCORRECT_OLD_PASSWORD = "Old password is incorrect";
    public static final String ROLE_NOT_FOUND = "Role not found: ";
}
