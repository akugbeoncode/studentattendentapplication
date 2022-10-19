package com.omo9ice.studentattendantapplication;

public class LoggedUserDetails {
    private String grade, email, fullname, student_ref, mobile, password, guardian, guardian_mobile;

    public LoggedUserDetails(String grade, String email, String fullname, String student_ref, String mobile, String password, String guardian, String guardian_mobile) {
        this.grade = grade;
        this.email = email;
        this.fullname = fullname;
        this.student_ref = student_ref;
        this.mobile = mobile;
        this.password = password;
        this.guardian = guardian;
        this.guardian_mobile = guardian_mobile;
    }

    public LoggedUserDetails() {
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public String getStudent_ref() {
        return student_ref;
    }

    public void setStudent_ref(String student_ref) {
        this.student_ref = student_ref;
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

    public String getGuardian() {
        return guardian;
    }

    public void setGuardian(String guardian) {
        this.guardian = guardian;
    }

    public String getGuardian_mobile() {
        return guardian_mobile;
    }

    public void setGuardian_mobile(String guardian_mobile) {
        this.guardian_mobile = guardian_mobile;
    }
}
