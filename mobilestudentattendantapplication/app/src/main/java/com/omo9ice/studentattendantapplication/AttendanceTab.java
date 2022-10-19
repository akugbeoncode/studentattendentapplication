package com.omo9ice.studentattendantapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AttendanceTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AttendanceTab() {
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
    public static AttendanceTab newInstance(String param1, String param2) {
        AttendanceTab fragment = new AttendanceTab();
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
        View v = inflater.inflate(R.layout.fragment_attendance_tab, container, false);

        TextView broadcatingStatusTextView = (TextView) v.findViewById(R.id.broadcatingStatusTextView);
        ImageView broadcastingStatusImage = (ImageView) v.findViewById(R.id.broadcastingStatusImage);
        Button startAttendanceBtn = (Button) v.findViewById(R.id.startAttendanceBtn);
        Button StopAttendanceBtn = (Button) v.findViewById(R.id.StopAttendanceBtn);
        TextView studentAttRatLabel1 = (TextView) v.findViewById(R.id.studentAttRatLabel1);
        TextView studentAttendanceRatio = (TextView) v.findViewById(R.id.studentAttendanceRatio);
        TextView studentAttRatLabel2 = (TextView) v.findViewById(R.id.studentAttRatLabel2);
        LinearLayout statusColorCode = (LinearLayout) v.findViewById(R.id.statusColorCode);

        return v;
    }
}
