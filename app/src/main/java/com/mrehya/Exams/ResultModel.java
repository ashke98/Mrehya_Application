package com.mrehya.Exams;

/**
 * Created by hgjhghgjh on 11/20/2018.
 */

public class ResultModel {
    public ResultModel() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getResultshow() {
        return resultshow;
    }

    public void setResultshow(String resultshow) {
        this.resultshow = resultshow;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    int id;

    public String getIsAnswer() {
        return isanswer;
    }

    public void setIsAnswer(String isAnswer) {
        isanswer = isAnswer;
    }

    String title;
    String point;
    String isanswer;
    String resultshow;
    String message;


}
