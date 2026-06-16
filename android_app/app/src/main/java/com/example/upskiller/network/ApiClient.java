package com.example.upskiller.network;

import android.content.Context;

import com.example.upskiller.session.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Standard client — 30 s timeouts for all normal calls
    private static ApiService instance;

    // Long-timeout client — 3 minutes for AI roadmap generation
    private static ApiService longTimeoutInstance;

    public static synchronized ApiService get(Context context) {
        if (instance == null) {
            instance = buildService(context, 30, 30, 30);
        }
        return instance;
    }

    /**
     * Use this for endpoints that may take a long time (AI roadmap generation).
     * Shares the same auth interceptors but has a 3-minute read timeout.
     */
    public static synchronized ApiService getLongTimeout(Context context) {
        if (longTimeoutInstance == null) {
            longTimeoutInstance = buildService(context, 30, 180, 30);
        }
        return longTimeoutInstance;
    }

    private static ApiService buildService(Context context,
                                           int connectSec,
                                           int readSec,
                                           int writeSec) {
        SessionManager session = SessionManager.getInstance(context);

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectSec, TimeUnit.SECONDS)
                .readTimeout(readSec,    TimeUnit.SECONDS)
                .writeTimeout(writeSec,  TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(session))
                .authenticator(new TokenAuthenticator(session))
                .addInterceptor(logger)
                .build();

        return new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    private ApiClient() { /* singleton */ }
}