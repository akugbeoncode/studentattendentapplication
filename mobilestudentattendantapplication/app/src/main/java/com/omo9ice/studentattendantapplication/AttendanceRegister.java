package com.omo9ice.studentattendantapplication;

import com.google.firebase.Timestamp;

public class AttendanceRegister {
    private String classroom_ref;
    private String course_ref;
    private String student_ref;
    private Timestamp registration_date;

    public AttendanceRegister(String classroom_ref, String course_ref, String student_ref, Timestamp registration_date) {
        this.classroom_ref = classroom_ref;
        this.course_ref = course_ref;
        this.student_ref = student_ref;
        this.registration_date = registration_date;
    }

    public AttendanceRegister() {
    }

    public String getClassroom_ref() {
        return classroom_ref;
    }

    public void setClassroom_ref(String classroom_ref) {
        this.classroom_ref = classroom_ref;
    }

    public String getCourse_ref() {
        return course_ref;
    }

    public void setCourse_ref(String course_ref) {
        this.course_ref = course_ref;
    }

    public String getStudent_ref() {
        return student_ref;
    }

    public void setStudent_ref(String student_ref) {
        this.student_ref = student_ref;
    }

    public Timestamp getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Timestamp registration_date) {
        this.registration_date = registration_date;
    }
}
