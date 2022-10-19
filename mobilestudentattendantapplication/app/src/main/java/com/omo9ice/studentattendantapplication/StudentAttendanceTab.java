package com.omo9ice.studentattendantapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class StudentAttendanceTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentAttendanceTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabOneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentAttendanceTab newInstance(String param1, String param2) {
        StudentAttendanceTab fragment = new StudentAttendanceTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_attendance_tab, container, false);

        TextView syncStatusTextView = (TextView) v.findViewById(R.id.syncStatusTextView);
        ImageView syncStatusImage = (ImageView) v.findViewById(R.id.syncStatusImage);
        LinearLayout statusColorCode = (LinearLayout) v.findViewById(R.id.statusColorCode);
        Button startAttendanceBtn = (Button) v.findViewById(R.id.startAttendanceBtn);
        Button StopAttendanceBtn = (Button) v.findViewById(R.id.StopAttendanceBtn);
        Button markAttendance = (Button) v.findViewById(R.id.markAttendance);

        return v;
    }
}
