package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class InstructorClassroomAdapter extends FragmentPagerAdapter {

    private int behavior;

    public InstructorClassroomAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        this.behavior = behavior;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                AttendanceTab attendanceTab = new AttendanceTab();
                return attendanceTab;
            case 1:
                QuizTab quizTab = new QuizTab();
                return quizTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return behavior;
    }
}
