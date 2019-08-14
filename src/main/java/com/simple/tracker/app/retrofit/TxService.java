package com.simple.tracker.app.retrofit;

import com.simple.tracker.app.retrofit.value.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TxService {
    @GET("/api")
    Call<Response> getTransactions(@Query(value = "module") String module,
                                   @Query(value = "action") String action,
                                   @Query(value = "address") String address,
                                   @Query(value = "startblock") long startBlock,
                                   @Query(value = "endblock") long endBlock,
                                   @Query(value = "sort") String sort,
                                   @Query(value = "apikey") String apiKey);
}
