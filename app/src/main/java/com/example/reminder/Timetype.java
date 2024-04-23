package com.example.reminder;

import android.annotation.SuppressLint;

public class Timetype {
    int hour, min;
    public Timetype(int hour, int min) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hours must be between 0 and 23");
        }
        if (min< 0 || min > 59) {
            throw new IllegalArgumentException("Minutes must be between 0 and 59");
        }
        this.hour = hour;
        this.min = min;
    }
    public Timetype() {}
    public void setTime(int h, int m) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hours must be between 0 and 23");
        }
        if (min< 0 || min > 59) {
            throw new IllegalArgumentException("Minutes must be between 0 and 59");
        }
        hour = h;
        min = m;
    }
    public String getTime() {
        int H = this.hour;
        String h, m, a;
        if(H == 0) {
            h = "12";
            a = "AM";
        }
        else if(H>0 && H<12) {
            h = String.format("%02d", H);
            a = "AM";
        }
        else if(H == 12) {
            h = "12";
            a = "PM";
        }
        else {
            h = String.format("%02d", H-12);
            a = "PM";
        }
        m = String.format("%02d",this.min);
        return (h+":"+m+" "+a);
    }
    public int getDuration(Timetype tLat) {
        int dur;
        if(tLat.min >= min && tLat.hour >= hour)
            dur =  (tLat.hour - hour)*60 + tLat.min - min;
        else if (tLat.min < min && tLat.hour > hour)
            dur = (tLat.hour - hour - 1)*60 + 60 + tLat.min - min;
        else  //tLat.hour < hour
            dur = (60-min) + (tLat.min) + 60*(23 - hour + tLat.hour);
        return dur;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
