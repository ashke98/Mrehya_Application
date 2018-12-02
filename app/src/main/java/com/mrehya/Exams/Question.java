package com.mrehya.Exams;

import java.util.ArrayList;

/**
 * Created by sdfsdfasf on 2/27/2018.
 */

public class Question {

    String question, time;
    answer  ans1,ans2,ans3,ans4,ans5;
    int id;

    ArrayList<answer> answers;
    public Question(String question, int id, String time) {
        this.question = question;
        this.id = id;
        this.time = time;
        answers=new ArrayList<>();
    }

    public Question(String question, answer ans1, answer ans2, answer ans3, answer ans4, answer ans5, int id, String time) {
        this.question = question;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.ans5 = ans5;
        this.id = id;
        this.time = time;
    }

    public void setAnswer(answer ans){
        answers.add(ans);
    }
    public ArrayList<answer> getAnswers(){
        return answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public answer getans1() {
        return ans1;
    }
    public void setans1(answer ans1) {
        this.ans1 = ans1;
    }

    public answer getans2() {
        return ans2;
    }
    public void setans2(answer ans2) {
        this.ans2 = ans2;
    }

    public answer getans3() {
        return ans3;
    }
    public void setans3(answer ans3) {
        this.ans3 = ans3;
    }

    public answer getans4() {
        return ans4;
    }
    public void setans4(answer ans4) {
        this.ans4 = ans4;
    }

    public answer getans5() {
        return ans5;
    }
    public void setans5(answer ans5) {
        this.ans5 = ans5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static class answer
    {
        public answer(int id, int questionId, int point, String text, String texten) {
            this.id = id;
            this.questionId = questionId;
            this.point = point;
            this.text = text;
            this.texten = texten;
        }
        public answer() {
        }

        public int id,questionId,point;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuestionId() {
            return questionId;
        }

        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }

        public int getPoint() {
            return point;
        }

        public void setPoint(int point) {
            this.point = point;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTexten() {
            return texten;
        }

        public void setTexten(String texten) {
            this.texten = texten;
        }

        public String text,texten;

    }
}
