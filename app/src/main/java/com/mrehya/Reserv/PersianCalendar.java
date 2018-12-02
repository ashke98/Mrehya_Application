package com.mrehya.Reserv;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PersianCalendar {

    String[] weekDayNames = {
            "شنبه","یکشنبه","دوشنبه",
            "سه شنبه", "چهارشنبه",
            "پنج شنبه", "جمعه"
    };
    String[] monthNames ={
            "فروردین","اردیبهشت","خرداد","تیر", "مرداد","شهریور",
            "مهر", "آبان", "آذر","دی", "بهمن","اسفند"
    };

    String[] weekDayNames_g = {
            "Saturday","Sunday","Monday",
            "Tuesday", "Wednesday",
            "Thursday", "Friday"
    };
    String strWeekDay = "";
    String strMonth = "";
    int day;
    int month;
    int year;
    int ld;
    Calendar calendar = Calendar.getInstance();
    int gregorianYear =calendar.get(Calendar.YEAR);
    int gregorianMonth = calendar.get(Calendar.MONTH)+1;
    int gregorianDate = calendar.get(Calendar.DATE);
    int WeekDay = calendar.get(Calendar.DAY_OF_WEEK);

    String weekDay;
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
    String weekDay2 = dayFormat.format(calendar.getTime());
    int weekday2 = 0;


    int[] buf1 = {0,31,59,90,120,151,181,212,243,273,304,334};
    int[] buf2 = {0,31,60, 91,121,152,182, 213, 244, 274,305,335};

    public PersianCalendar(){
        Date gregorianDate = new Date();
        calendar.setTime(gregorianDate);
        toPersian(gregorianDate);
    }

    public PersianCalendar(Date gregorianDate){
        calendar.setTime(gregorianDate);
        toPersian(gregorianDate);
    }

    private void toPersian(Date gregorianDate)
    {
        if ((gregorianYear % 4) != 0)
            func1();
        else
            func2();
        strMonth = monthNames[month-1];

        for(int i=0;i<7;i++){
            if(weekDay2 == weekDayNames_g[i]){
                weekday2=i;
                break;
            }
        }
        //Log.e("WEEK DAY ", "WEEK DAY IS: " + WeekDay + ", " + weekDay2);
        strWeekDay = weekDayNames[weekday2];
    }

    private void func1()
    {
        day = buf1[gregorianMonth - 1] + gregorianDate;
        if (day > 79){
            day = day - 79;
            if (day <= 186) {
                int day2 = day;
                month = (day2 / 31) + 1;
                day = (day2 % 31);
                if(day2 % 31 == 0){
                    month--;
                    day = 31;
                }
                year = gregorianYear - 621;
            }
            else {
                int day2 = day - 186;
                month = (day2 / 30) + 7;
                day = (day2 % 30);
                if(day2 % 30 == 0){
                    month = (day2 / 30) + 6;
                    day = 30;
                }
                year = gregorianYear - 621;
            }
        }
        else{
            ld = gregorianYear > 1996 && gregorianYear % 4 == 1 ? 11 : 10 ;
            int day2 = day + ld;
            month = (day2 / 30) + 10;
            day = (day2 % 30);
            if(day2 % 30 == 0)            {
                month--;
                day = 30;
            }
            year = gregorianYear - 622;
        }
    }

    private void func2()
    {
        day = buf2[gregorianMonth - 1] + gregorianDate;
        ld = gregorianYear >= 1996 ? 79 : 80 ;
        if (day > ld) {
            day = day - ld;
            if (day <= 186) {
                int day2 = day;
                month = (day2 / 31) + 1;
                day = (day2 % 31);
                if(day2 % 31 == 0){
                    month--;
                    day = 31;
                }
                year = gregorianYear - 621;
            } else {
                int day2 = day - 186;
                month = (day2 / 30) + 7;
                day = (day2 % 30);
                if(day2 % 30 == 0 ){
                    month--;
                    day = 30;
                }
                year = gregorianYear - 621;
            }
        }
        else {
            int day2 = day + 10;
            month = (day2 / 30) + 10;
            day = (day2 % 30);
            if(day2 % 30==0){
                month--;
                day = 30;
            }
            year = gregorianYear - 622;
        }
    }

    public int getYear(){
        return year;
    }
    public int getMonth(){
        return month;
    }
}