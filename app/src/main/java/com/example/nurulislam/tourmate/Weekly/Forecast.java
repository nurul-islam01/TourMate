package com.example.nurulislam.tourmate.Weekly;

import com.example.nurulislam.tourmate.Weather;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forecast {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("high")
    @Expose
    private String high;
    @SerializedName("low")
    @Expose
    private String low;
    @SerializedName("text")
    @Expose
    private String text;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    float oldHightTmp;

    public String getHigh() {

        if (Weather.units =="metric"){
            if (oldHightTmp ==0.0) {
                float f = Float.parseFloat(high);
                float tmp = (5 * (f - 32)) / 9f;
                oldHightTmp = tmp;
                high = String.valueOf(String.format("%.1f", tmp));
                return high + "°c";
            }else {

                return String.format("%.1f",oldHightTmp)+ "°c";
            }
        }else {
            return  high+"F";
        }
    }

    public void setHigh(String high) {
        this.high = high;
    }
    float oldLowTmp;
    public String getLow() {
        if (Weather.units =="metric"){
            if (oldLowTmp ==0.0) {
                float f1 = Float.parseFloat(low);
                float tmp1 = (5 * (f1 - 32)) / 9f;
                oldLowTmp = tmp1;
                low = String.valueOf(String.format("%.1f", tmp1));
                return low + "°c";
            }else {

                return String.format("%.1f",oldLowTmp)+ "°c";
            }
        }else {
            return  low+"F";
        }
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}