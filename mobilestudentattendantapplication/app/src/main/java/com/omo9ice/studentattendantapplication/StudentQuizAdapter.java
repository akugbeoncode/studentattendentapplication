package com.omo9ice.studentattendantapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class StudentQuizAdapter extends BaseAdapter {
    Context context;
    int layoutRes;
    List<Quiz> quizList;
    ListView quizListView;

    public StudentQuizAdapter(Context context, int layoutRes, List<Quiz> quizList, ListView quizListView) {
        this.context = context;
        this.layoutRes = layoutRes;
        this.quizList = quizList;
        this.quizListView = quizListView;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return quizList.size();
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
            view = inflater.inflate(R.layout.instructor_quiz_list, null);
        }

        final Quiz quiz = quizList.get(i);

        TextView textViewQuizTitle = (TextView) view.findViewById(R.id.textViewQuizTitle);
        TextView textViewQuestion = (TextView) view.findViewById(R.id.textViewQuestion);

        textViewQuizTitle.setText(quiz.getTitle());
        textViewQuestion.setText(quiz.getQuestion().length() > 47 ? "Description :\n" + quiz.getQuestion().substring(0, 47) + "..." : quiz.getQuestion());

        return view;
    }
}
