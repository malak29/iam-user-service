package com.iam.user.config;

public final class Messages {

    private Messages () {}

    // Success messages
    public static final String USER_CREATED_SUCCESS = "User created successfully";
    public static final String USER_UPDATED_SUCCESS = "User updated successfully";
    public static final String USER_DELETED_SUCCESS = "User deleted successfully";
    public static final String USER_RETRIEVED_SUCCESS = "User retrieved successfully";
    public static final String USERS_RETRIEVED_SUCCESS = "Users retrieved successfully";

    // Error messages
    public static final String USER_NOT_FOUND = "User not found with ID: %s";
    public static final String USER_NOT_FOUND_EMAIL = "User not found with email: %s";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists: %s";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists: %s";
    public static final String INVALID_USER_DATA = "Invalid user data provided";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred while processing your request";

    // Validation messages
    public static final String INVALID_USER_ID = "Invalid user ID format";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String REQUIRED_FIELD_MISSING = "Required field is missing: %s";

}
