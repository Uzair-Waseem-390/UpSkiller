package com.example.upskiller.network;

import android.content.Context;

import com.example.upskiller.session.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiService instance;

    public static synchronized ApiService get(Context context) {
        if (instance == null) {
            SessionManager session = SessionManager.getInstance(context);

            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(session))
                    .authenticator(new TokenAuthenticator(session))
                    .addInterceptor(logger)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return instance;
    }

    private ApiClient() { /* singleton */ }
}
