package com.simple.tracker.app.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class TxCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        System.out.println("response = " + response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
    }
}
