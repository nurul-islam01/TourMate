package com.example.nurulislam.tourmate.POJO;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Event implements Serializable {
    private String eventName;
    private String stratingLocation;
    private String destination;
    private String startDate;
    private String depratureDate;
    private double budget;
    private String eventId;

    public Event(String eventName, String stratingLocation, String destination, String startDate, String depratureDate, double budget, String eventId) {
        this.eventName = eventName;
        this.stratingLocation = stratingLocation;
        this.destination = destination;
        this.startDate = startDate;
        this.depratureDate = depratureDate;
        this.budget = budget;
        this.eventId = eventId;
    }

    public Event() {
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDepratureDate() {
        return depratureDate;
    }

    public String getEventId() {
        return eventId;
    }



    public String getEventName() {
        return eventName;
    }

    public String getStratingLocation() {
        return stratingLocation;
    }

    public String getDestination() {
        return destination;
    }


    public double getBudget() {
        return budget;
    }
    public String countDay(){

        //set how days are left
        //count from currnet date

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        String createdDate = sdf.format(c.getTime());
        long day = 0;
        try {
            Date startDate = sdf.parse(createdDate);
            Date endDate = sdf.parse(depratureDate);
            long da = endDate.getTime()- startDate.getTime();
            day = TimeUnit.DAYS.convert(da, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day+" days to go";
    }
}
