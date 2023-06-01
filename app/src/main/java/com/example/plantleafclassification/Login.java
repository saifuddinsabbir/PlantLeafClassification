package com.example.plantleafclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    ImageView loginIcon;
    TextView loginMainText, loginSecondaryText, testText;
    TextInputLayout loginUserNameTextInputLayout, loginpasswordTextInputLayout;
    Button loginLoginButton, loginSignupButton;
    private Button signup, login;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUserNameTextInputLayout = findViewById(R.id.loginUserNameTextInputLayoutId);
        loginpasswordTextInputLayout = findViewById(R.id.loginpasswordTextInputLayoutId);

        signup = findViewById(R.id.signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Signup.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();
                loginUser();
            }
        });
    }
    private void checkInternetConnection() {
        try {
            String command = "ping -c 1 google.com";
            if(Runtime.getRuntime().exec(command).waitFor() != 0) {
                Toast.makeText(this, "Please connect to INTERNET", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Please connect to INTERNET", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validateUserName() {
        String val = loginUserNameTextInputLayout.getEditText().getText().toString();

        if (val.isEmpty()) {
            loginUserNameTextInputLayout.setError("Empty field");
            return false;
        } else {
            loginUserNameTextInputLayout.setError(null);
            loginUserNameTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {

        String val = loginpasswordTextInputLayout.getEditText().getText().toString();

        if (val.isEmpty()) {
            loginpasswordTextInputLayout.setError("Empty field");
            return false;
        } else {
            loginpasswordTextInputLayout.setError(null);
            loginpasswordTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser() {
        // hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        isUser();
    }

    private void isUser() {
        String userEnteredUsername = loginUserNameTextInputLayout.getEditText().getText().toString().trim();
        String userEnteredPassword = loginpasswordTextInputLayout.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("userName").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    loginUserNameTextInputLayout.setError(null);
                    loginUserNameTextInputLayout.setErrorEnabled(false);
                    loginpasswordTextInputLayout.setError(null);
                    loginpasswordTextInputLayout.setErrorEnabled(false);

                    String passwordFromDB = snapshot.child(userEnteredUsername).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {

                        //Toast.makeText(Login.this, "Logging in..", Toast.LENGTH_SHORT).show();
                        loginUserNameTextInputLayout.setError(null);
                        loginUserNameTextInputLayout.setErrorEnabled(false);

                        String dp = snapshot.child(userEnteredUsername).child("dp").getValue(String.class);
                        String fullNameFromDB = snapshot.child(userEnteredUsername).child("fullName").getValue(String.class);
                        String userNameFromDB = snapshot.child(userEnteredUsername).child("userName").getValue(String.class);
                        String dateOfBirthFromDB = snapshot.child(userEnteredUsername).child("dateOfBirth").getValue(String.class);
                        String emailFromDB = snapshot.child(userEnteredUsername).child("email").getValue(String.class);
                        String contactFromDB = snapshot.child(userEnteredUsername).child("contact").getValue(String.class);
                        String addressFromDB = snapshot.child(userEnteredUsername).child("address").getValue(String.class);

                        SessionManager sessionManager = new SessionManager(Login.this);
                        sessionManager.setProfileImage(dp);
                        sessionManager.createLoginSession(dp, fullNameFromDB, userNameFromDB, emailFromDB, contactFromDB, dateOfBirthFromDB, addressFromDB, passwordFromDB);

                        SharedPreferences.Editor editor = getSharedPreferences("UserInfo",
                                MODE_PRIVATE).edit();
                        editor.putString("userName", userEnteredUsername);
                        editor.apply();

                        Intent intent = new Intent(Login.this, Home.class);
                        intent.putExtra("userName", userEnteredUsername);
                        startActivity(intent);
                    } else {
                        loginpasswordTextInputLayout.setError("Wrong Password");
                        loginpasswordTextInputLayout.requestFocus();
                    }
                } else {
                    loginUserNameTextInputLayout.setError("No such User exist");
                    loginUserNameTextInputLayout.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}