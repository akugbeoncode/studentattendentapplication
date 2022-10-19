package com.omo9ice.studentattendantapplication;

public class Course {
    private String course_ref;
    private String code;
    private String taught_by;
    private String title;


    public Course(String course_ref, String code, String taught_by, String title) {
        this.course_ref = course_ref;
        this.code = code;
        this.taught_by = taught_by;
        this.title = title;
    }

    public Course() {
    }

    public String getCourse_ref() {
        return course_ref;
    }

    public void setCourse_ref(String course_ref) {
        this.course_ref = course_ref;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTaught_by() {
        return taught_by;
    }

    public void setTaught_by(String taught_by) {
        this.taught_by = taught_by;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
