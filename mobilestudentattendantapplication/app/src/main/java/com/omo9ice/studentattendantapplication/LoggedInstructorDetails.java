package com.omo9ice.studentattendantapplication;

public class LoggedInstructorDetails {
    private String device_code, email, fullname, instructor_ref, mobile, password;

    public LoggedInstructorDetails(String device_code, String email, String fullname, String instructor_ref, String mobile, String password) {
        this.device_code = device_code;
        this.email = email;
        this.fullname = fullname;
        this.instructor_ref = instructor_ref;
        this.mobile = mobile;
        this.password = password;
    }

    public LoggedInstructorDetails() {
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getInstructor_ref() {
        return instructor_ref;
    }

    public void setInstructor_ref(String instructor_ref) {
        this.instructor_ref = instructor_ref;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
