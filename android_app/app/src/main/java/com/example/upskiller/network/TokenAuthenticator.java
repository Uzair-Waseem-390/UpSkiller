package com.example.upskiller.network;

import com.example.upskiller.model.response.AuthResponse;
import com.example.upskiller.session.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

/**
 * Automatically refreshes the access token when a 401 is received,
 * then retries the original request. Clears the session if refresh fails.
 */
public class TokenAuthenticator implements Authenticator {

    private final SessionManager session;

    public TokenAuthenticator(SessionManager session) {
        this.session = session;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Avoid infinite loops — if we already retried once, give up.
        if (responseCount(response) >= 2) {
            session.clearSession();
            return null;
        }

        String refreshToken = session.getRefreshToken();
        if (refreshToken == null) {
            session.clearSession();
            return null;
        }

        // Build a fresh Retrofit instance WITHOUT the authenticator to avoid recursion.
        retrofit2.Retrofit refreshRetrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        ApiService refreshService = refreshRetrofit.create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("refresh_token", refreshToken);

        try {
            Call<AuthResponse> call = refreshService.refreshToken(body);
            retrofit2.Response<AuthResponse> res = call.execute();

            if (res.isSuccessful() && res.body() != null && res.body().getTokens() != null) {
                String newAccess  = res.body().getTokens().getAccess();
                String newRefresh = res.body().getTokens().getRefresh();
                session.saveTokens(newAccess, newRefresh);

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccess)
                        .build();
            }
        } catch (Exception ignored) { }

        session.clearSession();
        return null;
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) count++;
        return count;
    }
}
