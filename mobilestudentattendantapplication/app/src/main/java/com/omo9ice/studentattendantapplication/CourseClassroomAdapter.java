package com.omo9ice.studentattendantapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseClassroomAdapter extends BaseAdapter {
    private static final String TAG = "CCAdapter";
    Context context;
    int layoutRes;
    List<TimeTableInstance> timeTableList;
    ListView listView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private List<String> classroomCourses;

    public CourseClassroomAdapter(Context context, int layoutRes, List<TimeTableInstance> timeTableList, ListView listView) {
        this.context = context;
        this.layoutRes = layoutRes;
        this.timeTableList = timeTableList;
        this.listView = listView;
        this.classroomCourses = null;
    }

    public CourseClassroomAdapter(Context context, int layoutRes, List<TimeTableInstance> timeTableList, ListView listView, List<String> classroomCourses) {
        this.context = context;
        this.layoutRes = layoutRes;
        this.timeTableList = timeTableList;
        this.listView = listView;
        this.classroomCourses = classroomCourses;
    }

    @Override
    public int getCount() {
        return timeTableList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.instructor_course_list, null);
        }

        final TextView courseCodeTextView = (TextView) view.findViewById(R.id.courseCode);
        final TextView courseTitleTextView = (TextView) view.findViewById(R.id.courseName);
        final TextView startTextView = (TextView) view.findViewById(R.id.start);
        final TextView stopTextView = (TextView) view.findViewById(R.id.stop);
        final Button startClassButton = (Button) view.findViewById(R.id.startClass);

        final TimeTableInstance tti = timeTableList.get(i);
        final List<Course> courseList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        db.collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Course course = document.toObject(Course.class);
                                courseList.add(course);
                            }
                            assert courseList != null;
                            if (courseList != null) {
                                final Course targetCourse = getCourse(courseList, tti.getCourse_ref());
                                courseCodeTextView.setText(targetCourse.getCode());
                                courseTitleTextView.setText(targetCourse.getTitle());
                                startTextView.setText(tti.getStart());
                                stopTextView.setText(tti.getEnd());
                                startClassButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
//                                        new start new activity Here
                                    }
                                });
                            }

                            final String classroom = getCurrentlyActiveClassroom(classroomCourses, tti.getCourse_ref());
                            startClassButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent((InstructorDashboard)context, InstructorClassroom.class);
                                    intent.putExtra("CLASSROOM_REF", classroom);
                                    intent.putExtra("COURSE_REF", tti.getCourse_ref());
                                    ((InstructorDashboard)context).startActivity(intent);
                                    ((InstructorDashboard)context).finish();
                                }
                            });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return view;
    }

    private String getCurrentlyActiveClassroom(List<String> classroomCourses, String course_ref) {
        if (classroomCourses != null && classroomCourses.size() > 0) {
            String targetClassroomCourse = null;
            for (int i=0; i<classroomCourses.size(); i++) {
                if (classroomCourses.get(i).contains(course_ref)) {
                    targetClassroomCourse = classroomCourses.get(i);
                }
            }
            if (targetClassroomCourse != null) {
                String[] classCourseSplit = targetClassroomCourse.split(",");
                return classCourseSplit[0];
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private Course getCourse(List<Course> courseList, String course_ref) {
        Course deCourse = null;
        for (int i=0; i<courseList.size(); i++) {
            if (courseList.get(i).getCourse_ref().equals(course_ref)) {
                deCourse = courseList.get(i);
            }
        }
        return deCourse;
    }
}
