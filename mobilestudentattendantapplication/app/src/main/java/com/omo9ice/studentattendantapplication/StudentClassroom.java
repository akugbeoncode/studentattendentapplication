package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
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
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class StudentClassroom extends AppCompatActivity {

    private static final String TAG = "Student";
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private Toolbar toolbar;
    private TabLayout layout;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private LoggedUserDetails loggedUserDetails;
    private Classroom classroom;
    private String loggedUserID;
    private String classroom_ref;
    private String course_ref;
    private Course course;
    private LoggedInstructorDetails loggedInstructorDetails;

    private TextView syncStatusTextView, studentAttRatLabel1, studentAttendanceRatio, studentAttRatLabel2;
    private ImageView syncStatusImage;
    private Button startAttendanceBtn, stopAttendanceBtn, markAttendance;
    private LinearLayout statusColorCode;

    private TextView quizListEmptyTextView;
    private ListView quizListView;

    private List<Quiz> quizList;
    private List<Quiz> todayQuiz;
    private Quiz quiz;

    private Date quizDate;
    private Date today;

    private boolean hasTakenAttendance;

    private StudentQuizAdapter studentQuizAdapter;

    Timer timer;

    BluetoothAdapter bluetoothAdapter;
    List<String> bluetoothDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classroom);

        timer = new Timer();

        classroom_ref = getIntent().getStringExtra("CLASSROOM_REF");
        course_ref = getIntent().getStringExtra("COURSE_REF");
        loggedUserID = "";

        quizList = new ArrayList<>();
        todayQuiz = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);

        hasTakenAttendance = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("classrooms")
                .whereEqualTo("classroom_ref", classroom_ref)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                classroom = document.toObject(Classroom.class);
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
                                loggedUserID = document.getId();
                                loggedUserDetails =  document.toObject(LoggedUserDetails.class);

                                try {
                                    setSupportActionBar(toolbar);
                                    getSupportActionBar().setTitle(loggedUserDetails.getFullname().toUpperCase());
                                    toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(StudentClassroom.this, StudentDashboard.class));
                                        }
                                    });
                                } catch (Exception ex) {}
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        if (layout.getTabCount() == 0) {
            layout.addTab(layout.newTab().setText("Attendance"));
            layout.addTab(layout.newTab().setText("Quiz"));
        }

        adapter = new StudentClassroomAdapter(getSupportFragmentManager(), layout.getTabCount());
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
//                            Check Permissions
                                checkBTPermissions();
                                bluetoothAdapter.startDiscovery();
                                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                                registerReceiver(receiver, filter);
                            } else {
                                showToast("Bluetooth is already on...");
                                if (bluetoothAdapter.isDiscovering()) {
                                    bluetoothAdapter.cancelDiscovery();
                                }
//                            Check Permissions
                                checkBTPermissions();
                                bluetoothAdapter.startDiscovery();
                                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                                registerReceiver(receiver, filter);
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

                    markAttendance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar cal1 = Calendar.getInstance();
                            Calendar cal2 = Calendar.getInstance();
                            cal1.setTime(new Date(System.currentTimeMillis()));

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

                                                boolean canTakeAttendance = true;
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                Date currentDate = new Date(System.currentTimeMillis());
                                                for (int i=0; i<attendanceRegisterList.size(); i++) {
                                                    if (attendanceRegister != null && sdf.format(currentDate).toString().equals(sdf.format(attendanceRegisterList.get(i).getRegistration_date().toDate()).toString()) && loggedUserDetails.getStudent_ref().equals(attendanceRegister.getStudent_ref())) {
                                                        canTakeAttendance = false;
                                                        markAttendance.setVisibility(View.GONE);
                                                        syncStatusTextView.setText("Attendance has been marked!!!");
                                                        hasTakenAttendance = true;
                                                    }
                                                }

                                                if  (canTakeAttendance) {
                                                    Map<String, Object> attendance_register = new HashMap<>();

                                                    attendance_register.put("classroom_ref", classroom_ref);
                                                    attendance_register.put("course_ref", course_ref);
                                                    attendance_register.put("registration_date", Timestamp.now());
                                                    attendance_register.put("student_ref", loggedUserDetails.getStudent_ref());

                                                    db.collection("attendance_register")
                                                            .add(attendance_register)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    System.out.println(" ===>>>>>     Attendance has been updated Successfully  =====>>  " + documentReference.getId());
                                                                    markAttendance.setVisibility(View.GONE);
                                                                    syncStatusTextView.setText("Attendance has been marked!!!");
                                                                    hasTakenAttendance = true;
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            System.out.println(" ===>>>>>     Attendance update failed  =====>>  " + e.getMessage());
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                        }
                    });
                } catch (Exception ex) {}

                if (tab.getPosition() == 0) {
                    manageCourseAttendanceRecord(viewPager);
                    updateAttendanceInformationIfBlueIsEnabled();
                }

                if (tab.getPosition() == 1) {
                    manageStudentquizSection(viewPager);
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

    private void manageStudentquizSection(ViewPager viewPager) {
        quizListEmptyTextView  = (TextView) viewPager.findViewById(R.id.quizListEmptyTextView);
        quizListView  = (ListView) viewPager.findViewById(R.id.quizListView);

        db.collection("quiz")
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

                                studentQuizAdapter = new StudentQuizAdapter(StudentClassroom.this, R.layout.fragment_quiz_tab, todayQuiz, quizListView);
                                quizListView.setAdapter(studentQuizAdapter);
                                studentQuizAdapter.notifyDataSetChanged();
                            }

                            quizListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    int viewPos = quizListView.getPositionForView(view);
                                    final Quiz quizInstance = todayQuiz.get(viewPos);
                                    Intent intent = new Intent(StudentClassroom.this, StudentQuizView.class);
                                    intent.putExtra("CREATED_ON", quizInstance.getCreated_on());
                                    intent.putExtra("CREATED_BY", quizInstance.getCreated_by());
                                    intent.putExtra("CLASSROOM_REF", quizInstance.getClassroom_ref());
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println(" ===============================>>>>>>   onRecieve: ACTION_FOUND");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println(" ===============================>>>>>>   blue devices   "+ device.getName());
                bluetoothDevices.add(device.getName());
            }
        }
    };

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            } else {

            }
        }
    }

    private void updateAttendanceInformationIfBlueIsEnabled() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // use runOnUiThread(Runnable action)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(" ===============================>>>>>>   blue devices   "+ bluetoothDevices.size());
                        if (loggedInstructorDetails != null) {
                            System.out.println(" ===============================>>>>>>   instructor bluetooth id   "+ loggedInstructorDetails.getDevice_code());
                        }
                        try {
                            for (int i = 0; i < bluetoothDevices.size(); i++) {
                                System.out.println(" ===============================>>>>>>   blue address   " + bluetoothDevices.get(i).toString());
                            }
                        } catch(Exception ex) {}

                    }
                });
            }
        }, 0, 10000L);
    }

    private void manageCourseAttendanceRecord(ViewPager viewPager) {
        syncStatusTextView = (TextView) viewPager.findViewById(R.id.syncStatusTextView);
        syncStatusImage = (ImageView) viewPager.findViewById(R.id.syncStatusImage);
        statusColorCode = (LinearLayout) viewPager.findViewById(R.id.statusColorCode);
        startAttendanceBtn = (Button) viewPager.findViewById(R.id.startAttendanceBtn);
        stopAttendanceBtn = (Button) viewPager.findViewById(R.id.StopAttendanceBtn);
        markAttendance = (Button) viewPager.findViewById(R.id.markAttendance);

        if (bluetoothAdapter == null) {
            syncStatusTextView.setVisibility(View.VISIBLE);
            statusColorCode.setVisibility(View.VISIBLE);
            syncStatusImage.setVisibility(View.VISIBLE);
            syncStatusTextView.setText("Bluetooth is not Available");
            statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorInactiveBluetooth));
            syncStatusImage.setImageResource(R.drawable.ic_action_not_snyc);
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (isInstructorDeviceVisible()) {
                    syncStatusTextView.setVisibility(View.VISIBLE);
                    statusColorCode.setVisibility(View.VISIBLE);
                    syncStatusImage.setVisibility(View.VISIBLE);
                    stopAttendanceBtn.setVisibility(View.VISIBLE);
                    if (hasTakenAttendance) {
                        markAttendance.setVisibility(View.GONE);
                    } else {
                        markAttendance.setVisibility(View.VISIBLE);
                    }
                    startAttendanceBtn.setVisibility(View.GONE);

                    statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorActiveBluetooth));
                    syncStatusTextView.setText("Instructor Device Detected, Please mark attendance");
                    syncStatusImage.setImageResource(R.drawable.ic_action_snyc);
                } else {
                    syncStatusTextView.setVisibility(View.VISIBLE);
                    statusColorCode.setVisibility(View.VISIBLE);
                    syncStatusImage.setVisibility(View.VISIBLE);
                    stopAttendanceBtn.setVisibility(View.VISIBLE);
                    markAttendance.setVisibility(View.GONE);
                    startAttendanceBtn.setVisibility(View.GONE);

                    statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorInactiveBluetooth));
                    syncStatusTextView.setText("Searching for Instructor Device");
                    syncStatusImage.setImageResource(R.drawable.ic_action_not_snyc);
                }
            } else {
                syncStatusTextView.setVisibility(View.VISIBLE);
                statusColorCode.setVisibility(View.VISIBLE);
                syncStatusImage.setVisibility(View.VISIBLE);
                stopAttendanceBtn.setVisibility(View.GONE);
                markAttendance.setVisibility(View.GONE);
                startAttendanceBtn.setVisibility(View.VISIBLE);

                statusColorCode.setBackgroundColor(getResources().getColor(R.color.colorInactiveBluetooth));
                syncStatusTextView.setText("Status: Inactive");
                syncStatusImage.setImageResource(R.drawable.ic_action_not_snyc);
            }
        }
    }

    private boolean isInstructorDeviceVisible() {
        boolean instructorDeviceIsAvailable = false;
        db.collection("courses")
                .whereEqualTo("course_ref", course_ref)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                course = document.toObject(Course.class);
                            }

                            if (course == null) {} else {
                                db.collection("instructors")
                                        .whereGreaterThanOrEqualTo("instructor_ref", course.getTaught_by())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        loggedInstructorDetails = document.toObject(LoggedInstructorDetails.class);
                                                    }
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
       try {
           if (loggedInstructorDetails != null && bluetoothDevices.size() > 0) {
               for (int i=0; i<bluetoothDevices.size(); i++) {
                   if (bluetoothDevices.get(i).toString().equals(loggedInstructorDetails.getDevice_code())) {
                       instructorDeviceIsAvailable = true;
                   }
               }
           }
       } catch (Exception ex) {}
        return instructorDeviceIsAvailable;
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
                } else {
                    showToast("could't on bluetooth");
                }
                break;
        }
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
        startActivity(new Intent(StudentClassroom.this, MainActivity.class)); //Go back to home page
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        try {
            unregisterReceiver(receiver);
        } catch (Exception ex) {}
    }
}