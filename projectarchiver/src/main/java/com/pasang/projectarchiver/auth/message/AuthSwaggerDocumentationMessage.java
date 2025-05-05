package com.pasang.projectarchiver.auth.message;

public class AuthSwaggerDocumentationMessage {
    private AuthSwaggerDocumentationMessage() {}

    public static final String CREATE_LOGIN_SUMMARY = "User Login";
    public static final String CREATE_LOGIN_DESCRIPTION = "Authenticates a user using email and password, then returns JWT access tokens and message.";

    public static final String CREATE_LOGOUT_SUMMARY = "User Logout";
    public static final String CREATE_LOGOUT_DESCRIPTION = "Logs out a user by invalidating the JWT access token. This action does not require authentication and can be performed by any user.";


}
