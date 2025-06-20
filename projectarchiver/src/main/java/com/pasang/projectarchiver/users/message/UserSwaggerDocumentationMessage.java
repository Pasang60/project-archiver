package com.pasang.projectarchiver.users.message;

public class UserSwaggerDocumentationMessage {
    public static final String UPDATE_USER_SUMMARY = "Update User";
    public static final String UPDATE_USER_DESCRIPTION = "Update the profile of the user";
    public static final String GET_USER_BY_ID_SUMMARY = "Get User by ID";
    public static final String GET_USER_BY_ID_DESCRIPTION = "Get the user details by user ID";

    private UserSwaggerDocumentationMessage() {}

    public static final String ADD_USER_SUMMARY = "Add a new user";
    public static final String ADD_USER_DESCRIPTION = "Submit a request to add a new user";

    public static final String VALIDATE_OTP_SUMMARY = "Validate OTP by the User";
    public static final String VALIDATE_OTP_DESCRIPTION = "Validate OTP by the User";
    public static final String SET_PASSWORD_SUMMARY = "Set Password by User";
    public static final String SET_PASSWORD_DESCRIPTION = "Submit password as a request to set password for newly created user";

}
