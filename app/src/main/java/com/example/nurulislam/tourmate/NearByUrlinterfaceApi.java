package com.example.nurulislam.tourmate;

import com.example.nurulislam.tourmate.NEARBY.NearBy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NearByUrlinterfaceApi {
    @GET
    Call<NearBy> nearByUrlApi(@Url String urlString);
}
