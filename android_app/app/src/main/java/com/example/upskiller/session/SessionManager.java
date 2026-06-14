package com.example.upskiller.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.upskiller.model.Tokens;
import com.example.upskiller.model.User;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME     = "upskiller_prefs";
    private static final String KEY_ACCESS    = "access_token";
    private static final String KEY_REFRESH   = "refresh_token";
    private static final String KEY_USER      = "user_json";

    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                       .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    // ── Tokens ──────────────────────────────────────────────────────────────

    public void saveTokens(Tokens tokens) {
        prefs.edit()
             .putString(KEY_ACCESS,  tokens.getAccess())
             .putString(KEY_REFRESH, tokens.getRefresh())
             .apply();
    }

    public void saveTokens(String access, String refresh) {
        prefs.edit()
             .putString(KEY_ACCESS,  access)
             .putString(KEY_REFRESH, refresh)
             .apply();
    }

    public String getAccessToken()  { return prefs.getString(KEY_ACCESS,  null); }
    public String getRefreshToken() { return prefs.getString(KEY_REFRESH, null); }

    // ── User ─────────────────────────────────────────────────────────────────

    public void saveUser(User user) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply();
    }

    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        return json != null ? gson.fromJson(json, User.class) : null;
    }

    // ── Session state ────────────────────────────────────────────────────────

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
