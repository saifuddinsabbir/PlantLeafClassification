package com.example.plantleafclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Signup extends AppCompatActivity {

    private Button signup;
    private EditText usernameSignup, emailSignup, passwordSignup, contactSignup;
    TextInputLayout signupFullNameTextInputLayout, signUpUsernameTextInputLayout, signUpEmailTextInputLayout,
            signUpPhoneNoTextInputLayout, signupPasswordTextInputLayout, signupConfirmPasswordTextInputLayout, dateofbirth;
    TextInputEditText signupFullNameTextEditText, signUpUsernameTextInputEditText, signUpEmailTextInputEditText,
            signUpPhoneNoTextEditText, signupPasswordTextEditText, signupConfirmPasswordTextEditText, signupDobTextEditText;

    private Boolean clicked = false, z;
    private String date = "";

    DatabaseReference userReference;

    DatePickerDialog.OnDateSetListener setListener;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        signupFullNameTextInputLayout = findViewById(R.id.signupFullNameTextInputLayoutId);
        signUpUsernameTextInputLayout = findViewById(R.id.signUpUsernameTextInputLayoutId);
        dateofbirth = findViewById(R.id.dateOfBirth);
        signUpEmailTextInputLayout = findViewById(R.id.signUpEmailTextInputLayoutId);
        signUpPhoneNoTextInputLayout = findViewById(R.id.signUpPhoneNoTextInputLayoutId);
        signupPasswordTextInputLayout = findViewById(R.id.signupPasswordTextInputLayoutId);
        signupConfirmPasswordTextInputLayout = findViewById(R.id.signupConfirmPasswordTextInputLayoutId);
        signUpUsernameTextInputEditText = findViewById(R.id.signUpUsernameTextInputEditTextId);
        signUpEmailTextInputEditText = findViewById(R.id.signUpEmailTextInputEditTextId);
        signupFullNameTextEditText = findViewById(R.id.signupFullNameTextEditTextId);
        signUpPhoneNoTextEditText = findViewById(R.id.signUpPhoneNoTextEditTextId);
        signupPasswordTextEditText = findViewById(R.id.signupPasswordTextEditTextId);
        signupConfirmPasswordTextEditText = findViewById(R.id.signupConfirmPasswordTextEditTextId);
        signupDobTextEditText = findViewById(R.id.signUpDobTextInputEditTextId);

        userReference = FirebaseDatabase.getInstance().getReference("users");

        fullNameCheck();
        userNameCheck();
        dobCheck();
        emailCheck();
        phoneNoCheck();
        passwordCheck();
        confirmPasswordCheck();

        signup = findViewById(R.id.signup_btn2);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                storeOnDatabase();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year =calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day =calendar.get(Calendar.DAY_OF_MONTH);

        signupDobTextEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Signup.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view , int year , int month, int dayOfMonth) {
                        month = month+ 1;
                        date = day + "." + month + "." + year;
                        signupDobTextEditText.setText(date);
                    }
                },year, month,day);
                datePickerDialog.show();
            }
        });



    }

    private void fullNameCheck() {
        signupFullNameTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String typename = String.valueOf(editable);

                if (typename.isEmpty()) {
                    signupFullNameTextInputLayout.setError("Empty field");
                } else {
                    signupFullNameTextInputLayout.setError(null);
                    signupFullNameTextInputLayout.setErrorEnabled(false);
                }
            }
        });
    }


    private void userNameCheck() {
        signUpUsernameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                DatabaseReference mData = FirebaseDatabase.getInstance().getReference("users");
                String typename = String.valueOf(editable);
                String noWhiteSpace = "\\A\\w{4,20}\\z";

                if (typename.isEmpty()) {
                    signUpUsernameTextInputLayout.setError("Empty field");
                } else if (typename.length() >= 15) {
                    signUpUsernameTextInputLayout.setError("Username is too long");
                } else if (!typename.matches(noWhiteSpace)) {
                    signUpUsernameTextInputLayout.setError("White spaces are not allowed");
                } else {
                    signUpUsernameTextInputLayout.setError(null);
                    signUpUsernameTextInputLayout.setErrorEnabled(false);
                }

                mData.orderByChild("userName").equalTo(typename).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && !clicked) {
                            signUpUsernameTextInputLayout.setError("Username already exists");
                        }  else {
                            signUpUsernameTextInputLayout.setError(null);
                            signUpUsernameTextInputLayout.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void dobCheck() {
        signupDobTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if (date.isEmpty()) {
                    dateofbirth.setError("Empty field");
                } else {
                    dateofbirth.setError(null);
                    dateofbirth.setErrorEnabled(false);
                }
            }
        });
    }


    private void emailCheck() {
        signUpEmailTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String typename = String.valueOf(editable);

                if (typename.isEmpty()) {
                    signUpEmailTextInputLayout.setError("Empty field");
                } else if (!typename.matches(emailPattern)) {
                    signUpEmailTextInputLayout.setError("Invalid email address");
                } else {
                    signUpEmailTextInputLayout.setError(null);
                    signUpEmailTextInputLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void phoneNoCheck() {
        signUpPhoneNoTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String typename = String.valueOf(editable);

                if (typename.isEmpty()) {
                    signUpPhoneNoTextInputLayout.setError("Empty field");
                } else if (typename.length() != 11) {
                    signUpPhoneNoTextInputLayout.setError("Invalid phone number");
                } else {
                    signUpPhoneNoTextInputLayout.setError(null);
                    signUpPhoneNoTextInputLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void passwordCheck() {
        signupPasswordTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String typename = String.valueOf(editable);
                String passwordVal = "^" +
                        //"(?=.*[0-9])" +         //at least 1 digit
                        //"(?=.*[a-z])" +         //at least 1 lower case letter
                        //"(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{4,}" +               //at least 4 characters
                        "$";


                if (typename.isEmpty()) {
                    signupPasswordTextInputLayout.setError("Empty field");
                } else if (!typename.matches(passwordVal)) {
                    signupPasswordTextInputLayout.setError("Password is too weak");
                } else {
                    signupPasswordTextInputLayout.setError(null);
                    signupPasswordTextInputLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void confirmPasswordCheck() {
        signupConfirmPasswordTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String typename = String.valueOf(editable);

                if (typename.isEmpty()) {
                    signupConfirmPasswordTextInputLayout.setError("Empty field");
                } else if (!typename.equals(signupPasswordTextInputLayout.getEditText().getText().toString())) {
                    signupConfirmPasswordTextInputLayout.setError("Passwords aren't matched");
                } else {
                    signupConfirmPasswordTextInputLayout.setError(null);
                    signupConfirmPasswordTextInputLayout.setErrorEnabled(false);
                }
            }
        });
    }
    //---------------------------------------
    private Boolean validateFullName() {
        String val = signupFullNameTextInputLayout.getEditText().getText().toString();

        if (val.isEmpty()) {
            signupFullNameTextInputLayout.setError("Empty field");
            return false;
        } else {
            signupFullNameTextInputLayout.setError(null);
            signupFullNameTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean validateUserName() {
        String val = signUpUsernameTextInputLayout.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            signUpUsernameTextInputLayout.setError("Empty field");
            return false;
        } else if (val.length() >= 15) {
            signUpUsernameTextInputLayout.setError("Username is too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            signUpUsernameTextInputLayout.setError("White spaces are not allowed");
            return false;
        } else {
            signUpUsernameTextInputLayout.setError(null);
            signUpUsernameTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean isUser() {
        String userEnteredUsername = signUpUsernameTextInputLayout.getEditText().getText().toString();
        Query checkUser = userReference.orderByChild("userName").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    signUpUsernameTextInputLayout.setError("Username already exist");
                    z = false;
                } else {
                    z = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return z;
    }

    private Boolean validateDob() {
        String val = date;

        if (val.isEmpty()) {
            dateofbirth.setError("Empty field");
            return false;
        }else {
            dateofbirth.setError(null);
            dateofbirth.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = signUpEmailTextInputLayout.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            signUpEmailTextInputLayout.setError("Empty field");
            return false;
        } else if (!val.matches(emailPattern)) {
            signUpEmailTextInputLayout.setError("Invalid email address");
            return false;
        } else {
            signUpEmailTextInputLayout.setError(null);
            signUpEmailTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = signUpPhoneNoTextInputLayout.getEditText().getText().toString();

        if (val.isEmpty()) {
            signUpPhoneNoTextInputLayout.setError("Empty field");
            return false;
        } else if (val.length() != 11) {
            signUpPhoneNoTextInputLayout.setError("Invalid phone number");
            return false;
        } else {
            signUpPhoneNoTextInputLayout.setError(null);
            signUpPhoneNoTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = signupPasswordTextInputLayout.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";


        if (val.isEmpty()) {
            signupPasswordTextInputLayout.setError("Empty field");
            return false;
        } else if (!val.matches(passwordVal)) {
            signupPasswordTextInputLayout.setError("Password is too weak");
            return false;
        } else {
            signupPasswordTextInputLayout.setError(null);
            signupPasswordTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String val = signupConfirmPasswordTextInputLayout.getEditText().getText().toString();

        if (val.isEmpty()) {
            signupConfirmPasswordTextInputLayout.setError("Empty field");
            return false;
        } else if (!val.equals(signupPasswordTextInputLayout.getEditText().getText().toString())) {
            signupConfirmPasswordTextInputLayout.setError("Passwords aren't matched");
            return false;
        } else {
            signupConfirmPasswordTextInputLayout.setError(null);
            signupConfirmPasswordTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public void storeOnDatabase() {

        if (!validateFullName() | !validateUserName() | !validateDob() | !validateEmail() | !validatePhoneNo() | !validatePassword() | !validateConfirmPassword()) {
            return;
        }

        //get all values
        String fullName = signupFullNameTextInputLayout.getEditText().getText().toString();
        String userName = signUpUsernameTextInputLayout.getEditText().getText().toString();
        String email = signUpEmailTextInputLayout.getEditText().getText().toString();
        String contact = signUpPhoneNoTextInputLayout.getEditText().getText().toString();
        String dateOfBirth = date;
        String gender = "-";
        String district = "District";
        String subDistrict = "SubDistrict";
        String address = "-";
        String password = signupPasswordTextInputLayout.getEditText().getText().toString();

        UserHelperClass helperClass = new UserHelperClass(fullName, userName, dateOfBirth, email, contact, password, gender, district, subDistrict, address);

        userReference.child(userName).setValue(helperClass);

        Toast.makeText(this, "Signed up successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Signup.this, Login.class));
        finish();
    }


}