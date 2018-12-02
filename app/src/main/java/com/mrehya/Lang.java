package com.mrehya;

/**
 * Created by Rubick on 2/22/2018.
 */

public class Lang {
    int id;
    String name, level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Lang(String name) {

        this.id = id;
        this.name = name;
        this.level = level;
    }

    public Lang(String name, String level) {

        this.name = name;
        this.level = level;
    }
}
