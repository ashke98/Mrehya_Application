package com.mrehya.Articles;

/**
 * Created by Rubick on 1/27/2018.
 */

public class Article {

    private int id,image;
    private String title,name;

    public Article(int id, int image, String title, String name) {
        this.id = id;
        this.image = image;
        this.title = title;
    }

    public Article(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
