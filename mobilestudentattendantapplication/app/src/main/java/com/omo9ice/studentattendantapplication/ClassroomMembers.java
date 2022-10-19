package com.omo9ice.studentattendantapplication;

public class ClassroomMembers {
    private String classroom_ref;
    private String student_ref;

    public ClassroomMembers(String classroom_ref, String student_ref) {
        this.classroom_ref = classroom_ref;
        this.student_ref = student_ref;
    }

    public ClassroomMembers() {
    }

    public String getClassroom_ref() {
        return classroom_ref;
    }

    public void setClassroom_ref(String classroom_ref) {
        this.classroom_ref = classroom_ref;
    }

    public String getStudent_ref() {
        return student_ref;
    }

    public void setStudent_ref(String student_ref) {
        this.student_ref = student_ref;
    }
}
