package com.iam.user.config;

public final class ApiRoutes {

    private ApiRoutes () {} // Prevent instantiation

    public static final String  API_V1 = "/api/v1";

    public static final String USERS = API_V1 + "/users";
    public static final String USER_BY_ID = "/{userId}";
    public static final String USER_BY_EMAIL = "/email/{email}";
    public static final String USERS_BY_ORGANIZATION = "/organization/{orgId}";
    public static final String USERS_BY_DEPARTMENT = "/department/{departmentId}";
}
