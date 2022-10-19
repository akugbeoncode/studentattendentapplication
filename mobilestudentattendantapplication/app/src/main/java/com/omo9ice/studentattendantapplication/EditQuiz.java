package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditQuiz extends AppCompatActivity {

    private static final String TAG = "EditQuiz";
    private FirebaseFirestore db;
    private Quiz quiz, selectedQuiz;

    private Toolbar toolbar;

    private EditText editQiuzTitle, editQuizQuestion, editQiuzOptionA, editQiuzOptionB, editQiuzOptionC, editQiuzOptionD;
    private Button updateQuizBtn;
    private Spinner editSpinnerAnswer;
    private String quiz_id;

    private String created_by;
    private String classroom_ref;
    private long created_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Quiz");
        try {
            toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        } catch (Exception ex2) {}

        editQiuzTitle = (EditText) findViewById(R.id.editQiuzTitle);
        editQuizQuestion = (EditText) findViewById(R.id.editQuizQuestion);
        editQiuzOptionA = (EditText) findViewById(R.id.editQiuzOptionA);
        editQiuzOptionB = (EditText) findViewById(R.id.editQiuzOptionB);
        editQiuzOptionC = (EditText) findViewById(R.id.editQiuzOptionC);
        editQiuzOptionD = (EditText) findViewById(R.id.editQiuzOptionD);
        editSpinnerAnswer = (Spinner) findViewById(R.id.editSpinnerAnswer);
        updateQuizBtn = (Button) findViewById(R.id.updateQuizBtn);

        Intent intent = getIntent();
        created_by = intent.getStringExtra("CREATED_BY");
        classroom_ref = intent.getStringExtra("CLASSROOM_REF");
        created_on = intent.getLongExtra("CREATED_ON", 0);

        db = FirebaseFirestore.getInstance();


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
                                        selectedQuiz = quiz;
                                        quiz_id = document.getId();
                                    }
                                }
                            }

                            String[] arrayAnswer = { "Select Answer Option", "A", "B", "C", "D"};
                            List<String> spinnerArray = Arrays.asList(arrayAnswer);

                            ArrayAdapter spinnerAdapter = new ArrayAdapter(EditQuiz.this,android.R.layout.simple_spinner_item, spinnerArray);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            editSpinnerAnswer.setAdapter(spinnerAdapter);

                            editQiuzTitle.setText(selectedQuiz.getTitle());
                            editQuizQuestion.setText(selectedQuiz.getQuestion());
                            editQiuzOptionA.setText(selectedQuiz.getOption_a());
                            editQiuzOptionB.setText(selectedQuiz.getOption_b());
                            editQiuzOptionC.setText(selectedQuiz.getOption_c());
                            editQiuzOptionD.setText(selectedQuiz.getOption_d());
                            editSpinnerAnswer.setSelection(spinnerAdapter.getPosition(selectedQuiz.getAnswer()));

                            updateQuizBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String strTitle = editQiuzTitle.getText().toString().trim();
                                    final String strQuestion = editQuizQuestion.getText().toString().trim();
                                    final String strOptionA = editQiuzOptionA.getText().toString().trim();
                                    final String strOptionB = editQiuzOptionB.getText().toString().trim();
                                    final String strOptionC = editQiuzOptionC.getText().toString().trim();
                                    final String strOptionD = editQiuzOptionD.getText().toString().trim();
                                    final String strAnswer = editSpinnerAnswer.getSelectedItem().toString().trim();

                                    if (
                                            TextUtils.isEmpty(strTitle) ||
                                            TextUtils.isEmpty(strQuestion) ||
                                            TextUtils.isEmpty(strOptionA) ||
                                            TextUtils.isEmpty(strOptionB) ||
                                            TextUtils.isEmpty(strOptionC) ||
                                            TextUtils.isEmpty(strOptionD) ||
                                            strAnswer.equals("Select Answer Option")
                                    ) {
                                        Toast.makeText(EditQuiz.this, "All input fields are required", Toast.LENGTH_SHORT).show();
                                    } else {
                                        updateQuiz(strTitle, strQuestion, strOptionA, strOptionB, strOptionC, strOptionD, strAnswer, classroom_ref, created_by, quiz_id);
                                    }
                                }
                            });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void updateQuiz(String strTitle, String strQuestion, String strOptionA, String strOptionB, String strOptionC, String strOptionD, String strAnswer, String classroom_ref, String created_by, String quiz_id) {
        Map<String, Object> quizHashMap = new HashMap<>();
        final long created_on = System.currentTimeMillis();
        final String finClassroom_ref = classroom_ref;
        final String finCreated_by = created_by;

        quizHashMap.put("title", strTitle);
        quizHashMap.put("question", strQuestion);
        quizHashMap.put("option_a", strOptionA);
        quizHashMap.put("option_b", strOptionB);
        quizHashMap.put("option_c", strOptionC);
        quizHashMap.put("option_d", strOptionD);
        quizHashMap.put("answer", strAnswer);

        db.collection("quiz")
                .document(quiz_id)
                .set(quizHashMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(EditQuiz.this, QuizView.class);
                        intent.putExtra("CREATED_ON", created_on);
                        intent.putExtra("CREATED_BY", finCreated_by);
                        intent.putExtra("CLASSROOM_REF", finClassroom_ref);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditQuiz.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}