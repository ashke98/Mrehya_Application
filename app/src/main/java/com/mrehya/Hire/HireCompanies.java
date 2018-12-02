package com.mrehya.Hire;

/**
 * Created by Rubick on 1/27/2018.
 */

public class HireCompanies {

    private int id;
    private String title,companyName,time,place,image,desc;

    public HireCompanies(int id, String image, String title, String companyName, String time, String place, String desc) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.companyName = companyName;
        this.time = time;
        this.place = place;
        this.desc = desc;
    }

    public HireCompanies(String companyName, String image) {
        this.companyName = companyName;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getdesc() {
        return desc;
    }

    public void setdesc(String desc) {
        this.desc = desc;
    }

}
