package com.example.upskiller.network;

import com.example.upskiller.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Attaches the Bearer access token to every outgoing request. */
public class AuthInterceptor implements Interceptor {

    private final SessionManager session;

    public AuthInterceptor(SessionManager session) {
        this.session = session;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = session.getAccessToken();
        Request original = chain.request();

        if (token == null) return chain.proceed(original);

        Request authenticated = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authenticated);
    }
}
