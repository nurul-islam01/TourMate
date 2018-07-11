package com.example.nurulislam.tourmate;

import com.example.nurulislam.tourmate.SELECTED_DISTANCE.SelectPlace;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface SelectPlaceApi {
    @GET
    Call<SelectPlace> SetSelectPlaceUrl(@Url String urlString);
}
