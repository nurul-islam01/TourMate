package com.example.nurulislam.tourmate;

import com.example.nurulislam.tourmate.Currnet.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CurrnetApi {
    @GET()
    Call<CurrentWeather> getCurrentApi(@Url String urlString);

}
