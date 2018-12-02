package com.mrehya.Reserv;

import android.content.Context;
import android.widget.Button;

import com.goodiebag.horizontalpicker.HorizontalPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ashke on 2/14/2018.
 */

public class Persian_Date_Methods {

    private String Language="fa";
    private ArrayList<String> years;
    private ArrayList<String> month;
    private PersianCalendar pc;
    public Persian_Date_Methods(String Language){
        years= new ArrayList<>();
        month= new ArrayList<>();
        this.Language = Language;
        pc = new PersianCalendar();
    }

    public Persian_Date_Methods(){
    }

    public List<Button> day_btn(int day_counts, Context context){
        ArrayList<Button> daysbtns = new ArrayList<Button> ();
        ArrayList<String> days = new ArrayList<String> ();
        days.add("۱" ); days.add("۲" ); days.add("۳"); days.add("۴");
        days.add("۵" ); days.add("۶" ); days.add("۷"); days.add("۸");
        days.add("۹" ); days.add("۱۰"); days.add("۱۱");days.add("۱۲");
        days.add("۱۳"); days.add("۱۴"); days.add("۱۵");days.add("۱۶");
        days.add("۱۷"); days.add("۱۸"); days.add("۱۹");days.add("۲۰");
        days.add("۲۱"); days.add("۲۲"); days.add("۲۳");days.add("۲۴");
        days.add("۲۵"); days.add("۲۶"); days.add("۲۷");days.add("۲۸");
        days.add("۲۹"); days.add("۳۰"); days.add("۳۱");
        for(int day=0;day<=day_counts;day++) {
            Button btn = new Button(context);
            btn.setText(days.get(day));
            daysbtns.add(btn);
        }
        return daysbtns;
    }


    public List<HorizontalPicker.PickerItem> day(int day_counts){
        ArrayList<String> days = new ArrayList<String> ();
        days.add("۱" ); days.add("۲" ); days.add("۳"); days.add("۴");
        days.add("۵" ); days.add("۶" ); days.add("۷"); days.add("۸");
        days.add("۹" ); days.add("۱۰"); days.add("۱۱");days.add("۱۲");
        days.add("۱۳"); days.add("۱۴"); days.add("۱۵");days.add("۱۶");
        days.add("۱۷"); days.add("۱۸"); days.add("۱۹");days.add("۲۰");
        days.add("۲۱"); days.add("۲۲"); days.add("۲۳");days.add("۲۴");
        days.add("۲۵"); days.add("۲۶"); days.add("۲۷");days.add("۲۸");
        days.add("۲۹"); days.add("۳۰"); days.add("۳۱");
        List<HorizontalPicker.PickerItem> day_textItems = new ArrayList<>();
        for(int day=0;day<=day_counts;day++) {
            day_textItems.add(new HorizontalPicker.TextItem(days.get(day)));
        }
        return day_textItems;
    }

    public String get_month(int index){
        index-=1;
        month = new ArrayList<String> ();
        month.add("فروردین" ); month.add("اردیبهشت" ); month.add("خرداد"); month.add("تیر");
        month.add("مرداد" ); month.add("شهریور" ); month.add("مهر"); month.add("آبان");
        month.add("آذر" ); month.add("دی"); month.add("بهمن");month.add("اسفند");
        return month.get(index);
    }

    public int get_month_number(String month_name){
        month = new ArrayList<String> ();
        month.add("فروردین" ); month.add("اردیبهشت" ); month.add("خرداد"); month.add("تیر");
        month.add("مرداد" ); month.add("شهریور" ); month.add("مهر"); month.add("آبان");
        month.add("آذر" ); month.add("دی"); month.add("بهمن");month.add("اسفند");
        return month.indexOf(month_name);
    }



    public String get_month_number2(String month_name){
        ArrayList<String> month = new ArrayList<String> ();
        month.add("Jan" ); month.add("Feb" ); month.add("Mar"); month.add("Apr ");
        month.add("May" ); month.add("Jun" ); month.add("Jul"); month.add("Aug");
        month.add("Sep" ); month.add("Oct"); month.add("Nov");month.add("Dec");
        int num=month.indexOf(month_name)+1;
        String sNumber=num+"";
        if( num<10)
            sNumber="0"+sNumber;
        return sNumber;
    }



    public String convertDate(String date){
        String newDate=date.split(" ")[5]+"-"+get_month_number2(date.split(" ")[1])+"-"+date.split(" ")[2];
        return  newDate;
    }


    public String get_day(int index){
        ArrayList<String> days = new ArrayList<String> ();
        days.add("۱" ); days.add("۲" ); days.add("۳"); days.add("۴");
        days.add("۵" ); days.add("۶" ); days.add("۷"); days.add("۸");
        days.add("۹" ); days.add("۱۰"); days.add("۱۱");days.add("۱۲");
        days.add("۱۳"); days.add("۱۴"); days.add("۱۵");days.add("۱۶");
        days.add("۱۷"); days.add("۱۸"); days.add("۱۹");days.add("۲۰");
        days.add("۲۱"); days.add("۲۲"); days.add("۲۳");days.add("۲۴");
        days.add("۲۵"); days.add("۲۶"); days.add("۲۷");days.add("۲۸");
        days.add("۲۹"); days.add("۳۰"); days.add("۳۱");
        return days.get(index);
    }
    public int day_counts(int month_number, int year){
        int daycount = 29;
        if(month_number<=6)
            daycount=30;
        else if(month_number==12)
            daycount=28;
        if(daycount==29 && year%4==0)
            daycount=30;
        return  daycount;
    }

    public ArrayList<String> get_hours(){
        ArrayList<String> hours = new ArrayList<String> ();
        hours.add("۸:۳۰" ); hours.add("۹:۰۰" ); hours.add("۹:۳۰"); hours.add("۱۰:۰۰");
        hours.add("۱۰:۳۰" ); hours.add("۱۱:۰۰" ); hours.add("۱۱:۳۰"); hours.add("۱۲:۰۰");
        hours.add("۱۶:۰۰"); hours.add("۱۶:۳۰");hours.add("۱۷:۰۰");
        hours.add("۱۷:۳۰"); hours.add("۱۸:۰۰"); hours.add("۱۸:۳۰");hours.add("۱۹:۰۰");
        return hours;
    }

    public int get_persian_year(){
        return  pc.getYear();
    }

    public int get_gregorian_year(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public ArrayList<String> get_persian_years(){
        for (int i = get_persian_year(); i >= 1350; i--) {
            years.add(topersian_number(i+""));
        }
        return years;
    }
    public ArrayList<String> get_gregorian_years(){
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1350; i--) {
            years.add(i+"");
        }
        return years;
    }

    public ArrayList<String> get_persian_month(){
        month = new ArrayList<String> ();
        month.add("فروردین" ); month.add("اردیبهشت" ); month.add("خرداد"); month.add("تیر");
        month.add("مرداد" ); month.add("شهریور" ); month.add("مهر"); month.add("آبان");
        month.add("آذر" ); month.add("دی"); month.add("بهمن");month.add("اسفند");
        return month;
    }

    public ArrayList<String> get_gregorian_months(){
        month = new ArrayList<String> ();
        month.add("Jan" ); month.add("Feb" ); month.add("Mar"); month.add("Apr ");
        month.add("May" ); month.add("Jun" ); month.add("Jul"); month.add("Aug");
        month.add("Sep" ); month.add("Oct"); month.add("Nov");month.add("Dec");
        return month;
    }

    private String topersian_number(String number){
        String result="";
        for(int i=0;i<number.length();i++){
            result+=en_num_to_p(number.charAt(i));
        }
        return result;
    }
    private char en_num_to_p(char number){
        switch (number){
            case '1':return '۱';
            case '2':return '۲';
            case '3':return '۳';
            case '4':return '۴';
            case '5':return '۵';
            case '6':return '۶';
            case '7':return '۷';
            case '8':return '۸';
            case '9':return '۹';
            default: return '۰';
        }
    }

}
