package com.omo9ice.studentattendantapplication;

public class StudentQuiz {
    private String quiz_id;
    private String student_id;
    private String answer;

    public StudentQuiz(String quiz_id, String student_id, String answer) {
        this.quiz_id = quiz_id;
        this.student_id = student_id;
        this.answer = answer;
    }

    public StudentQuiz() {
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
