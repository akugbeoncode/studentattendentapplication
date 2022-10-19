package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InstructorDashboard extends AppCompatActivity {
    private static final String TAG = "LoggedInstructor";
    private Toolbar toolbar;
    private ListView courseClassroomListView;
    private TextView noClassTextView;
    private List<String> classroomCourses;
    private List<Classroom> classroomList;
    private List<Course> courseList;
    private List<TimeTableInstance> timeTableList;
    private List<TimeTableInstance> instructorTimeTableList;
    private LoggedInstructorDetails loggedInstructorDetails;
    private CourseClassroomAdapter courseClassroomAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private String classroom_ref;
    private String timetable_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_dashboard);

        classroomList = new ArrayList<>();
        timeTableList = new ArrayList<>();
        courseList = new ArrayList<>();
        classroomCourses = new ArrayList<>();
        instructorTimeTableList = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        courseClassroomListView = (ListView) findViewById(R.id.courseForTheDayListView);
        noClassTextView = (TextView) findViewById(R.id.noClassTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("instructors")
            .whereEqualTo("email", firebaseUser.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            loggedInstructorDetails =  document.toObject(LoggedInstructorDetails.class);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });


        db.collection("classrooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Classroom classroom = document.toObject(Classroom.class);
                                classroomList.add(classroom);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        db.collection("courses")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Course course = document.toObject(Course.class);
                            if (course != null && loggedInstructorDetails != null) {
                                if (course.getTaught_by().equals(loggedInstructorDetails.getInstructor_ref())) {
                                    courseList.add(course);
                                }
                            }
                        }

                        db.collection("time_tables")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Calendar calendar = Calendar.getInstance();
                                            int day = calendar.get(Calendar.DAY_OF_WEEK);
                                            boolean hasBeenAssignedACourse = false;

                                            if (courseList.size() <= 0) {
                                                Log.d(TAG, " ===============>>>>>>>>>>>>    00000000000000000000000000 ################################## " +  courseList.size());
                                            }

                                            for (int j=0; j<courseList.size(); j++) {
                                                Log.d(TAG, " ===============>>>>>>>>>>>>     " +  courseList.get(j).getTaught_by() + " === "  + loggedInstructorDetails.getInstructor_ref() );
                                                if (courseList.get(j).getTaught_by().trim().equals(loggedInstructorDetails.getInstructor_ref().trim())) {
                                                    hasBeenAssignedACourse = true;
                                                }
                                            }

                                            if (hasBeenAssignedACourse) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    classroom_ref = document.get("classroom_ref").toString();
                                                    HashMap hashMap = (HashMap) document.get("timetable");
                                                    Object object = (Object) hashMap.get("timetable");
                                                    HashMap hashMap2 = (HashMap) object;
                                                    ArrayList arrayList = null;
                                                    switch (day) {
                                                        case Calendar.SUNDAY:
                                                        case Calendar.SATURDAY:
                                                            break;
                                                        case Calendar.MONDAY:
                                                            arrayList = (ArrayList) hashMap2.get("monday");
                                                            break;
                                                        case Calendar.TUESDAY:
                                                            arrayList = (ArrayList) hashMap2.get("tuesday");
                                                            break;
                                                        case Calendar.WEDNESDAY:
                                                            arrayList = (ArrayList) hashMap2.get("wednesday");
                                                            break;
                                                        case Calendar.THURSDAY:
                                                            arrayList = (ArrayList) hashMap2.get("thursday");
                                                            break;
                                                        case Calendar.FRIDAY:
                                                            arrayList = (ArrayList) hashMap2.get("friday");
                                                            break;
                                                    }

                                                    if (arrayList != null) {
                                                        for (int i = 0; i < arrayList.size(); i++) {
                                                            HashMap hashMap1 = (HashMap) arrayList.get(i);
                                                            TimeTableInstance tti = new TimeTableInstance(
                                                                    hashMap1.get("activity_ref").toString(),
                                                                    hashMap1.get("course_ref").toString(),
                                                                    hashMap1.get("end").toString(),
                                                                    hashMap1.get("start").toString(),
                                                                    hashMap1.get("label").toString()
                                                            );
                                                            if (tti.getActivity_ref().equals("long_break") || tti.getActivity_ref().equals("short_break")) {} else {
                                                                classroomCourses.add(classroom_ref + "," + tti.getCourse_ref());
                                                                timeTableList.add(tti);
                                                            }
                                                        }


                                                        assert timeTableList != null;
                                                        assert courseList != null;


                                                        setSupportActionBar(toolbar);
                                                        getSupportActionBar().setTitle(loggedInstructorDetails.getFullname().toUpperCase());
                                                        try {
                                                            toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                        } catch (Exception ex2) {}

                                                        instructorTimeTableList.clear();
                                                        for (int i = 0; i < timeTableList.size(); i++) {
                                                            for (int j = 0; j < courseList.size(); j++) {
                                                                if (timeTableList.get(i).getCourse_ref().equals(courseList.get(j).getCourse_ref())) {
                                                                    instructorTimeTableList.add(timeTableList.get(i));
                                                                }
                                                            }
                                                        }

                                                        if (instructorTimeTableList.isEmpty()) {
                                                            noClassTextView.setVisibility(View.VISIBLE);
                                                            courseClassroomListView.setVisibility(View.GONE);
                                                            switch (day) {
                                                                case Calendar.SUNDAY:
                                                                case Calendar.SATURDAY:
                                                                    noClassTextView.setText("Hi, " + loggedInstructorDetails.getFullname().toUpperCase() + ", Please do have a lovely WEEKEND \n\n and Stay safe.");
                                                                    break;
                                                                default:
                                                                    noClassTextView.setText("Hi, " + loggedInstructorDetails.getFullname().toUpperCase() + ", the Timetable says you do not have classes to teach to.\n\n Do have a pleasant day ahead.");
                                                                    break;
                                                            }
                                                        } else {
                                                            noClassTextView.setVisibility(View.GONE);
                                                            courseClassroomListView.setVisibility(View.VISIBLE);
                                                            courseClassroomAdapter = new CourseClassroomAdapter(InstructorDashboard.this, R.layout.instructor_course_list, instructorTimeTableList, courseClassroomListView, classroomCourses);
                                                            courseClassroomListView.setAdapter(courseClassroomAdapter);
                                                        }
                                                    }
                                                }
                                            }else {
                                                if (toolbar != null) {
                                                    setSupportActionBar(toolbar);
                                                    getSupportActionBar().setTitle(loggedInstructorDetails.getFullname().toUpperCase());
                                                    try {
                                                        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                    } catch (Exception ex2) {}
                                                    noClassTextView.setVisibility(View.VISIBLE);
                                                    courseClassroomListView.setVisibility(View.GONE);
                                                    noClassTextView.setText("You currently do not have courses assigned to you. Contact the system administrator for more information");
                                                }
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.instructor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu:
                handleLogoutRequest();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleLogoutRequest() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(InstructorDashboard.this, MainActivity.class)); //Go back to home page
        finish();
    }
}