package com.example.upskiller.network;

public final class ApiConstants {

    // Change this to your machine's local IP when testing on a physical device.
    // Use 10.0.2.2 for the Android emulator to reach localhost.
    // public static final String BASE_URL = "http://10.0.2.2:8000/";
    public static final String BASE_URL = "http://192.168.0.101:8000/";

    // Users
    public static final String REGISTER       = "api/users/auth/register/";
    public static final String LOGIN          = "api/users/auth/login/";
    public static final String LOGOUT         = "api/users/auth/logout/";
    public static final String REFRESH_TOKEN  = "api/users/auth/refresh/";
    public static final String PROFILE        = "api/users/profile/";
    public static final String PROFILE_DELETE = "api/users/profile/delete/";
    public static final String SKILLS         = "api/users/skills/";
    public static final String SKILLS_BULK    = "api/users/skills/bulk/";
    public static final String SKILLS_STATS   = "api/users/skills/stats/";

    // Roadmaps
    public static final String ROADMAP_CREATE    = "api/roadmaps/create/";
    public static final String ROADMAP_LIST      = "api/roadmaps/list/";
    public static final String ROADMAP_ACTIVE    = "api/roadmaps/active/";
    public static final String ROADMAP_COMPLETED = "api/roadmaps/completed/";

    private ApiConstants() { /* no instances */ }
}