package com.mrehya.Reserv;

import android.util.Log;

import com.github.sbahmani.jalcal.util.JalCal;

import java.util.ArrayList;

/**
 * Created by ashke98 on 8/2/2018.
 */

public class Day {
    public static String
            dayName,
            id,
            startTime,
            endTime,
            date,
            year,
            month,
            day,
            hour,
            minute,
            second;
    public static ArrayList<String> Hours;
    public static Persian_Date_Methods pd;
    public Day(String year, String month, String Day){
        this.year = year;
        this.month = month;
        this.day = Day;
        Hours=new ArrayList<>();
        pd = new Persian_Date_Methods();
    }

    public static ArrayList<String> getHours() {
        return Hours;
    }
    public static void setHour(String hour){
        Hours.add(hour);
    }

    public static String getDayName() {
        return dayName;
    }

    public static void setDayName(String dayName) {
        Day.dayName = dayName;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Day.id = id;
    }

    public static String getStartTime() {
        return startTime;
    }

    public static void setStartTime(String startTime) {
        Day.startTime = startTime;
    }

    public static String getEndTime() {
        return endTime;
    }

    public static void setEndTime(String endTime) {
        Day.endTime = endTime;
    }

    public static String getDate() {
        return date;
    }

    public static String getYear() {
        return year;
    }
    public static int getintYear() {
        return Integer.parseInt(year);
    }

    public static void setYear(String year) {
        Day.year = year;
    }

    public static String getMonth() {
        return month;
    }
    public static int getintMonth() {
        return Integer.parseInt(month);
    }

    public static void setMonth(String month) {
        Day.month = month;
    }

    public static String getDay() {
        return day;
    }
    public static int getintDay() {
        return Integer.parseInt(day);
    }


    public static void setDay(String day) {
        Day.day = day;
    }

    public static void setDate(String d){
         date=d;
    }
    public static String getdate(){
            //E/date: Tue Aug 07 00:00:00 GMT+04:30 2018
        try {
            //get_month_number2(Aug);
            String datetime =JalCal.jalaliToGregorian( getintYear(), getintMonth(), getintDay(), 0, 0, 0).toString();
            date = datetime.split(" ")[5]+"-"+pd.get_month_number2(datetime.split(" ")[1])+"-"+datetime.split(" ")[2];
           // Log.e("date", date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
