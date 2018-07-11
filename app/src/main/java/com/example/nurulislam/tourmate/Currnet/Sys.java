package com.example.nurulislam.tourmate.Currnet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sys {

    @SerializedName("message")
    @Expose
    private Double message;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("sunrise")
    @Expose
    private Integer sunrise;
    @SerializedName("sunset")
    @Expose
    private Integer sunset;

    public Double getMessage() {
        return message;
    }

    public void setMessage(Double message) {
        this.message = message;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String  getSunrise() {

        int unixSeconds = sunrise;
// convert seconds to milliseconds
        Date date = new Date(unixSeconds*1000L);
// the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm aaa");
// give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+6"));
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public void setSunrise(Integer sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {

        int unixSeconds = sunset;
// convert seconds to milliseconds
        Date date = new Date(unixSeconds*1000L);
// the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm aaa");
// give a timezone reference for formatting (see comment at the bottom)
//        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+6"));
        String formattedDate = sdf.format(date);

        return formattedDate;

    }

    public void setSunset(Integer sunset) {
        this.sunset = sunset;
    }

}