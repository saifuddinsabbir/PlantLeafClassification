package com.example.plantleafclassification;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progress;
    private Animation left_animation,right_animation;
    private ImageView logo;
    private TextView textView;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUsersDetailFromSession();

        progressBar = (ProgressBar) findViewById(R.id.progressbarID);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
            }
        });
        thread.start();

        //animation
        //logo = findViewById(R.id.logoId);
        textView = findViewById(R.id.titleplantID);
        left_animation = AnimationUtils.loadAnimation(this,R.anim.left_animation);
        right_animation = AnimationUtils.loadAnimation(this,R.anim.right_animation);

        //logo.setAnimation(left_animation);
        //textView.setAnimation(right_animation);
    }

    public void doWork() {

        for (progress=0; progress<=100; progress++){

            try {
                Thread.sleep(29);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void startApp(){

        if(sessionManager.checkLogin()) {
            Intent intent = new Intent(SplashScreen.this, Navbar.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashScreen.this,SlideView.class);
            startActivity(intent);
            finish();
        }
    }
}
