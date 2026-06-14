package com.example.upskiller.util;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Generic Retrofit callback that centralises success/error handling.
 * Subclasses only implement onSuccess() and onError().
 */
public abstract class ApiCallback<T> implements Callback<T> {

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            String message = parseError(response);
            onError(message);
        }
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        onError("Network error: " + t.getMessage());
    }

    public abstract void onSuccess(T body);
    public abstract void onError(String message);

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject json = new JSONObject(raw);
                if (json.has("error"))   return json.getString("error");
                if (json.has("detail"))  return json.getString("detail");
                if (json.has("message")) return json.getString("message");
                return raw;
            }
        } catch (Exception ignored) { }
        return "Error " + response.code();
    }
}
