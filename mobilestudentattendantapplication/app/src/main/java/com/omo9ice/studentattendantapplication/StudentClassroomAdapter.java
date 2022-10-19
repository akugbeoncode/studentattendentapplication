package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class StudentClassroomAdapter extends FragmentPagerAdapter {

    private int behavior;

    public StudentClassroomAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        this.behavior = behavior;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                StudentAttendanceTab attendanceTab = new StudentAttendanceTab();
                return attendanceTab;
            case 1:
                StudentQuizTab quizTab = new StudentQuizTab();
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
