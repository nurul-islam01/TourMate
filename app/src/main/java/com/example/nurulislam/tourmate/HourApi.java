package com.example.nurulislam.tourmate;

import com.example.nurulislam.tourmate.Hourly.HourlyForecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HourApi {
    @GET()
    Call<HourlyForecast> getHourlyApi(@Url String urlString);
}
