package com.omo9ice.studentattendantapplication;

import com.google.firebase.Timestamp;

public class Quiz {
    private String answer;
    private String question;
    private String title;
    private String option_a;
    private String option_b;
    private String option_c;
    private String option_d;
    private String classroom_ref;
    private String created_by;
    private long created_on;

    public Quiz(String answer, String question, String title, String option_a, String option_b, String option_c, String option_d, String classroom_ref, String created_by, long created_on) {
        this.answer = answer;
        this.question = question;
        this.title = title;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.classroom_ref = classroom_ref;
        this.created_by = created_by;
        this.created_on = created_on;
    }

    public Quiz() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public String getClassroom_ref() {
        return classroom_ref;
    }

    public void setClassroom_ref(String classroom_ref) {
        this.classroom_ref = classroom_ref;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public long getCreated_on() {
        return created_on;
    }

    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
}
