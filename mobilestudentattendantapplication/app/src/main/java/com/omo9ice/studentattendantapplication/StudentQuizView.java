package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentQuizView extends AppCompatActivity {

    private static final String TAG = "QuizView";
    private String created_by;
    private String classroom_ref;
    private String quiz_id;
    private long created_on;

    public Toolbar toolbar;

    private TextView textViewQuizTitle, quizQuestionTextView;

    private Button qiuzOptionABtn, qiuzOptionBBtn, qiuzOptionCBtn, qiuzOptionDBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private Quiz quiz, selectedQuiz;
    private List<StudentQuiz> studentQuizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_view);

        toolbar =  (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quiz");
        try {
            toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        } catch (Exception ex2) {}

        Intent intent = getIntent();
        created_by = intent.getStringExtra("CREATED_BY");
        classroom_ref = intent.getStringExtra("CLASSROOM_REF");
        created_on = intent.getLongExtra("CREATED_ON", 0);
        quiz_id = "";

        studentQuizList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        textViewQuizTitle = (TextView) findViewById(R.id.textViewQuizTitle);
        quizQuestionTextView = (TextView) findViewById(R.id.quizQuestionTextView);

        qiuzOptionABtn = (Button) findViewById(R.id.qiuzOptionABtn);
        qiuzOptionBBtn = (Button) findViewById(R.id.qiuzOptionBBtn);
        qiuzOptionCBtn = (Button) findViewById(R.id.qiuzOptionCBtn);
        qiuzOptionDBtn = (Button) findViewById(R.id.qiuzOptionDBtn);

        db.collection("quiz")
                .whereEqualTo("created_by", created_by)
                .whereEqualTo("classroom_ref", classroom_ref)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                quiz =  document.toObject(Quiz.class);
                                if (quiz != null) {
                                    Date quizDate = new Date(quiz.getCreated_on());
                                    Date quizDate1 = new Date(created_on);

                                    Calendar cal1 = Calendar.getInstance();
                                    Calendar cal2 = Calendar.getInstance();
                                    cal1.setTime(quizDate);
                                    cal2.setTime(quizDate1);

                                    boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

                                    if (sameDay) {
                                        quiz_id = document.getId();
                                        selectedQuiz = quiz;
                                    }
                                }
                            }

                            if (selectedQuiz != null) {
                                textViewQuizTitle.setText(selectedQuiz.getTitle());
                                quizQuestionTextView.setText(selectedQuiz.getQuestion());
                                qiuzOptionABtn.setText(selectedQuiz.getOption_a());
                                qiuzOptionBBtn.setText(selectedQuiz.getOption_b());
                                qiuzOptionCBtn.setText(selectedQuiz.getOption_c());
                                qiuzOptionDBtn.setText(selectedQuiz.getOption_d());
                            }

                            qiuzOptionABtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateStudentQuizSolutions(quiz_id, firebaseUser.getUid(), "A");
                                }
                            });

                            qiuzOptionBBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateStudentQuizSolutions(quiz_id, firebaseUser.getUid(), "B");
                                }
                            });

                            qiuzOptionCBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateStudentQuizSolutions(quiz_id, firebaseUser.getUid(), "C");
                                }
                            });

                            qiuzOptionDBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateStudentQuizSolutions(quiz_id, firebaseUser.getUid(), "D");
                                }
                            });


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void updateStudentQuizSolutions(final String quiz_id, final String uid, final String ans) {
        db.collection("student_quiz")
                .whereEqualTo("quiz_id", quiz_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            studentQuizList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                StudentQuiz studentQuiz=  document.toObject(StudentQuiz.class);
                                if (studentQuiz != null) {
                                    studentQuizList.add(studentQuiz);
                                }
                            }

                            boolean hasAnswered = false;

                            for (int i=0; i<studentQuizList.size(); i++) {
                                try {
                                    if (studentQuizList.get(i).getStudent_id().equals(uid)) {
                                        hasAnswered = true;
                                    }
                                } catch (Exception ex) {}
                            }

                            if (!hasAnswered) {
                                Map<String, Object> quiz = new HashMap<>();

                                quiz.put("answer", ans);
                                quiz.put("quiz_id", quiz_id);
                                quiz.put("student_ref", uid);

                                db.collection("student_quiz")
                                        .document()
                                        .set(quiz)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(StudentQuizView.this, "You has Answer this question successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
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
        startActivity(new Intent(StudentQuizView.this, MainActivity.class)); //Go back to home page
        finish();
    }
}