package com.example.plantleafclassification;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private Button about;
    private Button analyze,help,history;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view_ = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getContext());
        userDetails = sessionManager.getUsersDetailFromSession();

        about = view_.findViewById(R.id.about_button);
        analyze = view_.findViewById(R.id.analyze_button);
        help = view_.findViewById(R.id.help_button);
        history = view_.findViewById(R.id.history_button);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),About.class));
            }
        });
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Analyze.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Testing.class));
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),History.class));
            }
        });

        return view_;
    }
}