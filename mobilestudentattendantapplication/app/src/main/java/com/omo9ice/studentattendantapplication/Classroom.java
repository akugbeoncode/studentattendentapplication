package com.omo9ice.studentattendantapplication;

public class Classroom {
    private String classroom_ref;
    private String capacity;
    private String code;
    private String label;

    public Classroom(String classroom_ref, String capacity, String code, String label) {
        this.classroom_ref = classroom_ref;
        this.capacity = capacity;
        this.code = code;
        this.label = label;
    }

    public Classroom() {
    }

    public String getClassroom_ref() {
        return classroom_ref;
    }

    public void setClassroom_ref(String classroom_ref) {
        this.classroom_ref = classroom_ref;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
