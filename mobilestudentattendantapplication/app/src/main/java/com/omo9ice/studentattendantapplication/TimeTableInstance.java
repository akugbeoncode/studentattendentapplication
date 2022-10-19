package com.omo9ice.studentattendantapplication;

public class TimeTableInstance {
    private String activity_ref, course_ref, end, start, label;

    public TimeTableInstance(String activity_ref, String course_ref, String end, String start, String label) {
        this.activity_ref = activity_ref;
        this.course_ref = course_ref;
        this.end = end;
        this.start = start;
        this.label = label;
    }

    public String getActivity_ref() {
        return activity_ref;
    }

    public void setActivity_ref(String activity_ref) {
        this.activity_ref = activity_ref;
    }

    public String getCourse_ref() {
        return course_ref;
    }

    public void setCourse_ref(String course_ref) {
        this.course_ref = course_ref;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
