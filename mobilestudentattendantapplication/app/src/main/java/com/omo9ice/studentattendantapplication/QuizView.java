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
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizView extends AppCompatActivity {

    private static final String TAG = "QuizView";
    private String created_by;
    private String classroom_ref;
    private long created_on;

    public Toolbar toolbar;

    private TextView textViewQuizTitle, quizQuestionTextView, qiuzOptionATextView, qiuzOptionBTextView, qiuzOptionCTextView, qiuzOptionDTextView, qiuzAnswerTextView, qiuzSummaryTextView;

    private FirebaseFirestore db;
    private Quiz quiz, selectedQuiz;
    private List<StudentQuiz> studentQuizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_view);

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

        db = FirebaseFirestore.getInstance();

        studentQuizList = new ArrayList<>();

        textViewQuizTitle = (TextView) findViewById(R.id.textViewQuizTitle);
        quizQuestionTextView = (TextView) findViewById(R.id.quizQuestionTextView);
        qiuzOptionATextView = (TextView) findViewById(R.id.qiuzOptionATextView);
        qiuzOptionBTextView = (TextView) findViewById(R.id.qiuzOptionBTextView);
        qiuzOptionCTextView = (TextView) findViewById(R.id.qiuzOptionCTextView);
        qiuzOptionDTextView = (TextView) findViewById(R.id.qiuzOptionDTextView);
        qiuzAnswerTextView = (TextView) findViewById(R.id.qiuzAnswerTextView);
        qiuzSummaryTextView = (TextView) findViewById(R.id.qiuzSummaryTextView);


        db.collection("quiz")
                .whereEqualTo("created_by", created_by)
                .whereEqualTo("classroom_ref", classroom_ref)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String quiz_id = "";
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
                                        selectedQuiz = quiz;
                                        quiz_id = document.getId();
                                    }
                                }
                            }

                            if (selectedQuiz != null) {
                                textViewQuizTitle.setText(selectedQuiz.getTitle());
                                quizQuestionTextView.setText(selectedQuiz.getQuestion());
                                qiuzOptionATextView.setText(selectedQuiz.getOption_a());
                                qiuzOptionBTextView.setText(selectedQuiz.getOption_b());
                                qiuzOptionCTextView.setText(selectedQuiz.getOption_c());
                                qiuzOptionDTextView.setText(selectedQuiz.getOption_d());
                                qiuzAnswerTextView.setText(selectedQuiz.getAnswer());
                                getStudentPerformance(quiz_id, selectedQuiz.getAnswer());
                            }


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getStudentPerformance(String quiz_id, final String answer) {
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

                            int correctAnswers = 0;
                            for (int i=0; i<studentQuizList.size(); i++) {
                                if (studentQuizList.get(i).getAnswer().equals(answer)) {
                                    correctAnswers += 1;
                                }
                            }

                            if (studentQuizList.size() <= 0) {
                                qiuzSummaryTextView.setText("0 %");
                            } else {
                                qiuzSummaryTextView.setText(correctAnswers + "/" + studentQuizList.size());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            qiuzSummaryTextView.setText("0 %");
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.instructor_quiz_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu:
                handleLogoutRequest();
                return true;
            case R.id.editQuizMenu:
                loadEditQuizView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadEditQuizView() {
        Intent intent = new Intent(QuizView.this, EditQuiz.class);
        intent.putExtra("CREATED_ON", created_on);
        intent.putExtra("CREATED_BY", created_by);
        intent.putExtra("CLASSROOM_REF", classroom_ref);
        startActivity(intent);
    }

    private void handleLogoutRequest() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(QuizView.this, MainActivity.class)); //Go back to home page
        finish();
    }


}