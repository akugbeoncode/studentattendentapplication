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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentDashboard extends AppCompatActivity {
    private static final String TAG = "LoggedInstructor";
    private Toolbar toolbar;
    private ListView courseForTheDayListView;
    private TextView noClassTextView;
    private List<Course> courseList;
    private List<TimeTableInstance> studentTimeTableList;
    private List<String> classroomCourses;
    private ClassroomMembers classroomMembers;
    private LoggedUserDetails loggedUserDetails;
    private StudentCourseClassroomAdapter studentCourseClassroomAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        noClassTextView = findViewById(R.id.noClassTextView);
        courseForTheDayListView = (ListView) findViewById(R.id.courseForTheDayListView);
        courseList = new ArrayList<>();
        classroomCourses  = new ArrayList<>();
        studentTimeTableList  = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
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
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
                }
            });

        db.collection("students")
            .whereEqualTo("email", firebaseUser.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        loggedUserDetails =  document.toObject(LoggedUserDetails.class);
                    }

                    db.collection("classroom_members")
                        .whereEqualTo("student_ref", loggedUserDetails.getStudent_ref())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    classroomMembers = document.toObject(ClassroomMembers.class);
                                }

                                try {
                                    db.collection("time_tables")
                                        .whereEqualTo("classroom_ref", classroomMembers.getClassroom_ref())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Calendar calendar = Calendar.getInstance();
                                                final int day = calendar.get(Calendar.DAY_OF_WEEK);
                                                final List<TimeTableInstance> timeTableList = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
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
                                                                classroomCourses.add(classroomMembers.getClassroom_ref() + "," + tti.getCourse_ref());
                                                                timeTableList.add(tti);
                                                            }
                                                        }

                                                        assert loggedUserDetails != null;
                                                        assert timeTableList != null;
                                                        assert courseList != null;


                                                        setSupportActionBar(toolbar);
                                                        getSupportActionBar().setTitle(loggedUserDetails.getFullname().toUpperCase());
                                                        try {
                                                            toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                        } catch (Exception ex) {}

                                                        courseForTheDayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                            }
                                                        });


                                                        if (timeTableList.isEmpty()) {
                                                            noClassTextView.setVisibility(View.VISIBLE);
                                                            courseForTheDayListView.setVisibility(View.GONE);
                                                            switch (day) {
                                                                case Calendar.SUNDAY:
                                                                case Calendar.SATURDAY:
                                                                    noClassTextView.setText("Hi, " + loggedUserDetails.getFullname().toUpperCase() + ", Please do have a lovely WEEKEND \n\n and Stay safe.");
                                                                    break;
                                                                default:
                                                                    noClassTextView.setText("Hi, " + loggedUserDetails.getFullname().toUpperCase() + ", the timetable says you do not have classes today.\n\n Do have a pleasant day ahead.");
                                                                    break;
                                                            }
                                                        } else {
                                                            noClassTextView.setVisibility(View.GONE);
                                                            courseForTheDayListView.setVisibility(View.VISIBLE);
                                                            studentCourseClassroomAdapter = new StudentCourseClassroomAdapter(StudentDashboard.this, R.layout.instructor_course_list, timeTableList, courseForTheDayListView);
                                                            courseForTheDayListView.setAdapter(studentCourseClassroomAdapter);
                                                            studentCourseClassroomAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                            }
                                        });
                                } catch (Exception ex) {
                                    setSupportActionBar(toolbar);
                                    getSupportActionBar().setTitle(loggedUserDetails.getFullname().toUpperCase());
                                    try {
                                        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    } catch (Exception ex2) {}
                                    noClassTextView.setVisibility(View.VISIBLE);
                                    courseForTheDayListView.setVisibility(View.GONE);
                                    noClassTextView.setText("You have not been assign to a classroom, please contact the school authority for more information.");
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
        getMenuInflater().inflate(R.menu.student_menu, menu);
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
        startActivity(new Intent(StudentDashboard.this, MainActivity.class)); //Go back to home page
        finish();
    }
}