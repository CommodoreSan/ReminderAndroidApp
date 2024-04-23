package com.example.reminder;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Tasks {
    String taskname;
    public enum Day {Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday}
    Day day;
    Timetype starttime, endtime;
    String date;
    int type;
    boolean weekly, daily;
    int durmin;

    public Tasks(String taskname, String date, Timetype starttime, Timetype endtime, boolean weekly, boolean daily) {
        this.taskname = taskname;
        this.date = date;
        this.day = getDayOfWeek(date);
        this.starttime = starttime;
        this.endtime = endtime;
        this.weekly = weekly;
        this.daily = daily;
        this.durmin = starttime.getDuration(endtime);
        if(durmin < 10) type = 0;
        else if(durmin > 10 && durmin < 60) type = 1;
        else type = 2;
    }
    public static Day getDayOfWeek(String date) {
        Date d = null;
        Day day = null;
        try {
            d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace(); // Handle parsing exception
        }
        // Create a Calendar instance and set the parsed date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        // Get the day of the week (Sunday = 1, Monday = 2, ..., Saturday = 7)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch(dayOfWeek)
        {
            case 1 : day = Day.Sunday; break;
            case 2 : day = Day.Monday; break;
            case 3 : day = Day.Tuesday; break;
            case 4 : day = Day.Wednesday; break;
            case 5 : day = Day.Thursday; break;
            case 6 : day = Day.Friday; break;
            case 7 : day = Day.Saturday; break;
            default : break;
        }
        return day;
    }


    public Tasks() {}

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public Timetype getStarttime() {
        return starttime;
    }

    public void setStarttime(Timetype starttime) {
        this.starttime = starttime;
    }

    public Timetype getEndtime() {
        return endtime;
    }

    public void setEndtime(Timetype endtime) {
        this.endtime = endtime;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public boolean isDaily() {
        return daily;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public int getDurmin() {
        return durmin;
    }

    public void setDurmin(int durmin) {
        this.durmin = durmin;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
