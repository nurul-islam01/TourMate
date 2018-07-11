package com.example.nurulislam.tourmate;

import com.example.nurulislam.tourmate.PLACE_DETAILS.PlaceDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PlaceDetailsApi {
    @GET
    Call<PlaceDetails> setPlaceDetailApi(@Url String urlString);
}
