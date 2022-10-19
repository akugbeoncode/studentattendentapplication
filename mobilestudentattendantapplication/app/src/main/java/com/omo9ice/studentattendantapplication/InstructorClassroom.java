package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class InstructorClassroom extends AppCompatActivity {

    private static final String TAG = "Instructor";
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private Toolbar toolbar;
    private TabLayout layout;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    private TextView broadcatingStatusTextView, studentAttRatLabel1, studentAttendanceRatio, studentAttRatLabel2;
    private ImageView broadcastingStatusImage;
    private Button startAttendanceBtn, stopAttendanceBtn;
    private LinearLayout statusColorCode;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private LoggedInstructorDetails loggedInstructorDetails;
    private String loggedUserID;

    private String classroom_ref;
    private String course_ref;
    private Classroom classroom;
    private Course course;
    private List<ClassroomMembers> classroomMembersList;
    private List<AttendanceRegister> attendanceRegisterList;
    private AttendanceRegister attendanceRegister;
    private ClassroomMembers classroomMember;

    private Quiz quiz;
    private List<Quiz> quizList;

    Timer timer;

    private TextView quizListEmptyTextView;
    private ListView quizListView;
    private Button CreateQuizBtn;
    private QuizAdapter quizAdapter;

    private Date quizDate;
    private Date today;

    private List<Quiz> todayQuiz;


    private int blueToothDeviceUpdateStatus;

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_classroom);

        classroom_ref = getIntent().getStringExtra("CLASSROOM_REF");
        course_ref = getIntent().getStringExtra("COURSE_REF");
        loggedUserID = "";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        blueToothDeviceUpdateStatus = 0;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        timer = new Timer();

        quizDate = new Date();
        today = new Date();

        classroomMembersList  = new ArrayList<>();
        attendanceRegisterList = new ArrayList<>();
        quizList = new ArrayList<>();
        todayQuiz = new ArrayList<>();

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
                                loggedUserID = document.getId();
                                loggedInstructorDetails =  document.toObject(LoggedInstructorDetails.class);

                                try {
                                    setSupportActionBar(toolbar);
                                    getSupportActionBar().setTitle(loggedInstructorDetails.getFullname().toUpperCase());
                                    toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(InstructorClassroom.this, InstructorDashboard.class));
                                        }
                                    });
                                } catch (Exception ex) {}
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        if (!TextUtils.isEmpty(classroom_ref)) {
            db.collection("classrooms")
                    .whereEqualTo("classroom_ref", classroom_ref)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    classroom =  document.toObject(Classroom.class);
                                }

                                Calendar day = Calendar.getInstance();
                                day.set(Calendar.MILLISECOND, 0);
                                day.set(Calendar.SECOND, 0);
                                day.set(Calendar.MINUTE, 0);
                                day.set(Calendar.HOUR_OF_DAY, 0);
                                System.out.println(" ===============================>>>>>>           Timestamps           >>>>>   " + day.getTimeInMillis());
                                System.out.println(" ===============================>>>>>>           classroom_ref           >>>>>   " + classroom_ref);
                                System.out.println(" ===============================>>>>>>           course_ref           >>>>>   " + course_ref);

                                db.collection("classroom_members")
                                        .whereEqualTo("classroom_ref", classroom_ref)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                classroomMembersList.clear();
                                                if (task.isSuccessful()) {
                                                    classroomMembersList.clear();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        classroomMember =  document.toObject(ClassroomMembers.class);
                                                        if (classroomMember != null && classroomMember.getClassroom_ref().equals(classroom_ref.trim())) {
                                                            classroomMembersList.add(classroomMember);
                                                        }
                                                    }
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });


                                db.collection("attendance_register")
                                        .whereGreaterThanOrEqualTo("registration_date", day.getTimeInMillis())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    attendanceRegisterList.clear();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        attendanceRegister =  document.toObject(AttendanceRegister.class);
                                                        if (
                                                                attendanceRegister != null &&
                                                                        attendanceRegister.getClassroom_ref().equals(classroom_ref.trim()) &&
                                                                        attendanceRegister.getCourse_ref().equals(course_ref.trim())
                                                        ) {
                                                            attendanceRegisterList.add(attendanceRegister);
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



        if (layout.getTabCount() == 0) {
            layout.addTab(layout.newTab().setText("Attendance"));
            layout.addTab(layout.newTab().setText("Quiz"));
        }

        adapter = new InstructorClassroomAdapter(getSupportFragmentManager(), layout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(layout));
        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                try {
                    startAttendanceBtn = (Button) viewPager.findViewById(R.id.startAttendanceBtn);
                    stopAttendanceBtn = (Button) viewPager.findViewById(R.id.StopAttendanceBtn);

                    startAttendanceBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!bluetoothAdapter.isEnabled()) {
                                showToast("Turning On Bluetooth");
                                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(intent, REQUEST_ENABLE_BT);
                            } else {
                                showToast("Bluetooth is already on...");
                            }
                        }
                    });

                    stopAttendanceBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (bluetoothAdapter.isEnabled()) {
                                bluetoothAdapter.disable();
                                showToast("Turning Bluetooth off");
                                timer.cancel();
                                timer = new Timer();
                                manageCourseAttendanceRecord(viewPager);
                            } else {
                                showToast("Bluetooth is already off...");
                            }
                        }
                    });
                } catch (Exception ex) {}


                if (tab.getPosition() == 0) {
                    updateAttendanceInformationIfBlueIsEnabled();
                }

                if (tab.getPosition() == 1) {
                    manageQuizActivityforOngoingClass(viewPager);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void updateAttendanceInformationIfBlueIsEnabled() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // use runOnUiThread(Runnable action)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        manageCourseAttendanceRecord(viewPager);
                        updateAttendanceCounter();

                    }
                });
            }
        }, 0, 10000L);
    }

    private void updateAttendanceCounter() {
        db.collection("attendance_register")
            .whereEqualTo("course_ref", course_ref)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    AttendanceRegister attendanceRegister = null;
                    List<AttendanceRegister> attendanceRegisterList = new ArrayList<>();

                    if (attendanceRegisterList.size() > 0) {
                        attendanceRegisterList.clear();
                    }

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            attendanceRegister =  document.toObject(AttendanceRegister.class);

                            if (attendanceRegister != null) {
                                attendanceRegisterList.add(attendanceRegister);
                            }
                        }

                        int counter = 0;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date currentDate = new Date(System.currentTimeMillis());
                        for (int i=0; i<attendanceRegisterList.size(); i++) {
                            System.out.println(" ===============================>>>>>>       student ref        >>>>>   " + attendanceRegisterList.get(i).getStudent_ref());
                            System.out.println(" ===============================>>>>>>       student ref        >>>>>   " + sdf.format(currentDate).toString().equals(sdf.format(attendanceRegisterList.get(i).getRegistration_date().toDate()).toString()));
                            if (attendanceRegister != null && sdf.format(currentDate).toString().equals(sdf.format(attendanceRegisterList.get(i).getRegistration_date().toDate()).toString())) {
                                counter += 1;
                            }
                        }
                        if (counter > 0) {
                            studentAttendanceRatio.setText("" + counter + "/" + classroomMembersList.size());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }

    private void manageQuizActivityforOngoingClass(ViewPager viewPager) {
        quizListEmptyTextView  = (TextView) viewPager.findViewById(R.id.quizListEmptyTextView);
        quizListView  = (ListView) viewPager.findViewById(R.id.quizListView);
        CreateQuizBtn  = (Button) viewPager.findViewById(R.id.CreateQuizBtn);

        db.collection("quiz")
                .whereEqualTo("created_by", loggedInstructorDetails.getInstructor_ref())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            quizList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                quiz =  document.toObject(Quiz.class);
                                if (quiz != null) {
                                    quizList.add(quiz);
                                }
                            }

                            todayQuiz.clear();
                            if (quizList.size() > 0) {
                                for (int i=0; i<quizList.size(); i++) {

                                    try {
                                        quizDate = new Date(quizList.get(i).getCreated_on());
                                        today = new Date(System.currentTimeMillis());
                                    } catch (Exception ex) {
                                        quizDate = new Date(String.valueOf(quizList.get(i).getCreated_on()));
                                        today = new Date(System.currentTimeMillis());
                                    }

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                    if (sdf.format(quizDate).toString().equals(sdf.format(today).toString())) {
                                        todayQuiz.add(quizList.get(i));
                                    }
                                }
                            }

                            System.out.println(" ===============================>>>>>>     TodayQuiz  ===============>>>>>>>  " + todayQuiz.size());

                            if (todayQuiz.size() <= 0) {
                                quizListEmptyTextView.setVisibility(View.VISIBLE);
                                quizListView.setVisibility(View.GONE);
                            } else {
                                quizListEmptyTextView.setVisibility(View.GONE);
                                quizListView.setVisibility(View.VISIBLE);

                                quizAdapter = new QuizAdapter(InstructorClassroom.this, R.layout.fragment_quiz_tab, todayQuiz, quizListView);
                                quizListView.setAdapter(quizAdapter);
                                quizAdapter.notifyDataSetChanged();
                            }

                            quizListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    int viewPos = quizListView.getPositionForView(view);
                                    final Quiz quizInstance = todayQuiz.get(viewPos);
                                    Intent intent = new Intent(InstructorClassroom.this, QuizView.class);
                                    intent.putExtra("CREATED_ON", quizInstance.getCreated_on());
                                    intent.putExtra("CREATED_BY", quizInstance.getCreated_by());
                                    intent.putExtra("CLASSROOM_REF", quizInstance.getClassroom_ref());
                                    startActivity(intent);
                                }
                            });

                            CreateQuizBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(InstructorClassroom.this, CreateQuiz.class);
                                    intent.putExtra("CREATED_BY", loggedInstructorDetails.getInstructor_ref());
                                    intent.putExtra("CLASSROOM_REF", classroom.getClassroom_ref());
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void manageCourseAttendanceRecord(ViewPager viewPager) {
        broadcatingStatusTextView = (TextView) viewPager.findViewById(R.id.broadcatingStatusTextView);
        broadcastingStatusImage = (ImageView) viewPager.findViewById(R.id.broadcastingStatusImage);
        studentAttRatLabel1 = (TextView) viewPager.findViewById(R.id.studentAttRatLabel1);
        studentAttendanceRatio = (TextView) viewPager.findViewById(R.id.studentAttendanceRatio);
        studentAttRatLabel2 = (TextView) viewPager.findViewById(R.id.studentAttRatLabel2);
        statusColorCode = (LinearLayout) viewPager.findViewById(R.id.statusColorCode);

        if (bluetoothAdapter == null) {
            broadcatingStatusTextView.setVisibility(View.VISIBLE);
            statusColorCode.setVisibility(View.VISIBLE);
            broadcastingStatusImage.setVisibility(View.VISIBLE);
            broadcatingStatusTextView.setText("Bluetooth is not Available");
            statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorInactiveBluetooth));
            broadcastingStatusImage.setImageResource(R.drawable.ic_action_not_broadcasting);
        } else {
            if (bluetoothAdapter.isEnabled()) {
                broadcatingStatusTextView.setVisibility(View.VISIBLE);
                statusColorCode.setVisibility(View.VISIBLE);
                broadcastingStatusImage.setVisibility(View.VISIBLE);
                stopAttendanceBtn.setVisibility(View.VISIBLE);
                studentAttRatLabel1.setVisibility(View.VISIBLE);
                studentAttendanceRatio.setVisibility(View.VISIBLE);
                studentAttRatLabel2.setVisibility(View.VISIBLE);
                startAttendanceBtn.setVisibility(View.GONE);

                statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorActiveBluetooth));
                broadcatingStatusTextView.setText("Status: Active");
                broadcastingStatusImage.setImageResource(R.drawable.ic_action_broadcasting);


                updateBluetoothDeviceID();


                studentAttRatLabel1.setText("Attendance Status");
                if (attendanceRegisterList.size() > 0) {
                    studentAttendanceRatio.setText("" + attendanceRegisterList.size() + "/" + classroomMembersList.size());
                }
                studentAttRatLabel2.setText("students are in attendance.");
            } else {
                broadcatingStatusTextView.setVisibility(View.VISIBLE);
                statusColorCode.setVisibility(View.VISIBLE);
                broadcastingStatusImage.setVisibility(View.VISIBLE);
                stopAttendanceBtn.setVisibility(View.GONE);
                startAttendanceBtn.setVisibility(View.VISIBLE);
                studentAttRatLabel1.setVisibility(View.VISIBLE);
                studentAttendanceRatio.setVisibility(View.GONE);
                studentAttRatLabel2.setVisibility(View.GONE);

                statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorInactiveBluetooth));
                broadcatingStatusTextView.setText("Status: Inactive");
                broadcastingStatusImage.setImageResource(R.drawable.ic_action_not_broadcasting);
                studentAttRatLabel1.setText("Attendance information Services is currently not available. Please start attendance services above by clicking Start Attendance button above.");
            }
        }
    }

    private void updateBluetoothDeviceID() {
        String device_code = bluetoothAdapter.getName();
        System.out.println(" ===============================>>>>>>  Bluetooth ID  ===============>>>>>>>  " + bluetoothAdapter.getName());
        Map<String, Object> instructor = new HashMap<>();

        instructor.put("device_code", device_code);

        db.collection("instructors")
                .document(loggedUserID)
                .set(instructor, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println(" ===============================>>>>>>  Bluetooth ID  ===============>>>>>>>  Updated!!!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    showToast("Bluetooth is on ...");
                    Intent dIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
                    startActivity(dIntent);
                } else {
                    showToast("could't on bluetooth");
                }
                break;
        }
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
        startActivity(new Intent(InstructorClassroom.this, MainActivity.class)); //Go back to home page
        finish();
    }
}