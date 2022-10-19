package com.omo9ice.studentattendantapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateQuiz extends AppCompatActivity {

    private EditText quizQuestion, quizOptionA, quizOptionB, quizOptionC, quizOptionD, qiuzTitle;
    private Button createQuizBtn;
    private Spinner spinnerAnswer;

    FirebaseFirestore db;

    private String created_by;
    private String classroom_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Intent intent = getIntent();
        created_by = intent.getStringExtra("CREATED_BY");
        classroom_ref = intent.getStringExtra("CLASSROOM_REF");

        db = FirebaseFirestore.getInstance();

        qiuzTitle = (EditText) findViewById(R.id.qiuzTitle);
        quizQuestion = (EditText) findViewById(R.id.quizQuestion);
        quizOptionA = (EditText) findViewById(R.id.qiuzOptionA);
        quizOptionB = (EditText) findViewById(R.id.qiuzOptionB);
        quizOptionC = (EditText) findViewById(R.id.qiuzOptionC);
        quizOptionD = (EditText) findViewById(R.id.qiuzOptionD);
        spinnerAnswer = (Spinner) findViewById(R.id.spinnerAnswer);
        createQuizBtn  = (Button) findViewById(R.id.createQuizBtn);

        String[] arrayAnswer = { "Select Answer Option", "A", "B", "C", "D"};
        List<String> spinnerArray = Arrays.asList(arrayAnswer);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(CreateQuiz.this,android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnswer.setAdapter(spinnerAdapter);

        createQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String strTitle = qiuzTitle.getText().toString().trim();
                final String strQuestion = quizQuestion.getText().toString().trim();
                final String strOptionA = quizOptionA.getText().toString().trim();
                final String strOptionB = quizOptionB.getText().toString().trim();
                final String strOptionC = quizOptionC.getText().toString().trim();
                final String strOptionD = quizOptionD.getText().toString().trim();
                final String strAnswer = spinnerAnswer.getSelectedItem().toString().trim();

                if (
                        TextUtils.isEmpty(strTitle) ||
                        TextUtils.isEmpty(strQuestion) ||
                        TextUtils.isEmpty(strOptionA) ||
                        TextUtils.isEmpty(strOptionB) ||
                        TextUtils.isEmpty(strOptionC) ||
                        TextUtils.isEmpty(strOptionD) ||
                        strAnswer.equals("Select Answer Option")
                ) {
                    Toast.makeText(CreateQuiz.this, "All input fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    createQuiz(strTitle, strQuestion, strOptionA, strOptionB, strOptionC, strOptionD, strAnswer, classroom_ref, created_by);
                }
            }
        });


    }

    private void createQuiz(String strTitle, String strQuestion, String strOptionA, String strOptionB, String strOptionC, String strOptionD, String strAnswer, final String classroom_ref, String created_by) {

        Map<String, Object> quizHashMap = new HashMap<>();
        final long created_on = System.currentTimeMillis();
        final String finClassroom_ref = classroom_ref;
        final String finCreated_by = created_by;

        quizHashMap.put("title", strTitle);
        quizHashMap.put("classroom_ref", classroom_ref);
        quizHashMap.put("created_by", created_by);
        quizHashMap.put("created_on", created_on);
        quizHashMap.put("question", strQuestion);
        quizHashMap.put("option_a", strOptionA);
        quizHashMap.put("option_b", strOptionB);
        quizHashMap.put("option_c", strOptionC);
        quizHashMap.put("option_d", strOptionD);
        quizHashMap.put("answer", strAnswer);

        db.collection("quiz")
            .document()
            .set(quizHashMap)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    qiuzTitle.setText("");
                    quizQuestion.setText("");
                    quizOptionA.setText("");
                    quizOptionB.setText("");
                    quizOptionC.setText("");
                    quizOptionD.setText("");
                    spinnerAnswer.getAdapter().getItemId(0);

                    Intent intent = new Intent(CreateQuiz.this, QuizView.class);
                    intent.putExtra("CREATED_ON", created_on);
                    intent.putExtra("CREATED_BY", finCreated_by);
                    intent.putExtra("CLASSROOM_REF", finClassroom_ref);
                    startActivity(intent);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateQuiz.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }


}