package com.example.plantleafclassification;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class EditProfile extends AppCompatActivity {

    TextView profileFullnameTextView, profileUserNameTextView;
    TextInputLayout profileFullNameInputLayout, profileUserNameInputLayout, profileEmailInputLayout, profilePhoneNoInputLayout, profileDateOfBirthInputLayer, profileGenderInputLayout, profileBloodGroupInputLayout, profileAddressInputLayout, profilePasswordInputLayout;
    TextInputEditText dateOfBirthEditText;
    DatePickerDialog.OnDateSetListener setListener;

    ImageView profileBackButton, profileImage, updateProfileButton;
    ProgressBar profileProgressBar;

    String userNameGlobal;

    FirebaseDatabase database;
    FirebaseStorage storage;

    DatabaseReference reference;
    Query checkUser;

    String userImageFromDB,
            fullNameFromDB, fullNameFromET,
            userNameFromDB, userNameFromET,
            emailFromDB, emailFromET,
            phoneNoFromDB, phoneNoFromET,
            dateOfBirthFromDB, dateOfBirthFromET,
            addressFromDB, addressFromET,
            passwordFromDB, passwordFromET,
            districtFromDB, districtFromET,
            subDistrictFromDB, subDistrictFromET;

    AutoCompleteTextView formDistrictAutoCom, formSubDistrictAutoCom;
    DatabaseReference referenceLocation;
    ArrayList<String> spinnerList, spinnerList2;
    ArrayAdapter<String> adapter, adapter2;
    String district, subDistrict;

    Boolean isAnyValueChanged, finished = true;

    DatabaseReference referenceUser;
    StorageReference referencePostImage;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUsersDetailFromSession();
        userNameGlobal = userDetails.get(SessionManager.KEY_USERNAME);

        referenceUser = FirebaseDatabase.getInstance().getReference("users").child(userDetails.get(SessionManager.KEY_USERNAME));

        reference = FirebaseDatabase.getInstance().getReference("users");
        checkUser = reference.orderByChild("userName").equalTo(userNameGlobal);

        profileBackButton = findViewById(R.id.profileBackButtonId);
        profileImage = findViewById(R.id.profileImageId);
        profileFullNameInputLayout = findViewById(R.id.profileFullNameInputLayoutId);
        profileUserNameInputLayout = findViewById(R.id.profileUserNameInputLayoutId);
        profileEmailInputLayout = findViewById(R.id.profileEmailInputLayoutId);
        profilePhoneNoInputLayout = findViewById(R.id.profilePhoneNoInputLayoutId);
        profileDateOfBirthInputLayer = findViewById(R.id.profileDateOfBirthInputLayerId);
        profileAddressInputLayout = findViewById(R.id.profileAddressInputLayoutId);
        profilePasswordInputLayout = findViewById(R.id.profilePasswordInputLayoutId);
        updateProfileButton = findViewById(R.id.updateProfileButtonId);
        formDistrictAutoCom = findViewById(R.id.formDistrictAutoComId);
        formSubDistrictAutoCom = findViewById(R.id.formUpazilaAutoComId);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        profileImage.setAnimation(anim);

        spinnerList = new ArrayList<>();
        spinnerList2 = new ArrayList<>();

        adapter = new ArrayAdapter<String>(EditProfile.this, R.layout.drop_down_item, spinnerList);
        adapter2 = new ArrayAdapter<String>(EditProfile.this, R.layout.drop_down_item, spinnerList2);

        formDistrictAutoCom.setAdapter(adapter);
        formSubDistrictAutoCom.setAdapter(adapter2);

        showData();

        formDistrictAutoCom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spinnerList2.clear();
                district = formDistrictAutoCom.getText().toString();
                districtFromET = district;
                Toast.makeText(EditProfile.this, district, Toast.LENGTH_SHORT).show();
                showSecondList();
            }
        });

        formSubDistrictAutoCom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                subDistrict = formSubDistrictAutoCom.getText().toString();
                subDistrictFromET = subDistrict;
                //Toast.makeText(EditProfile.this, district + " " + subDistrict , Toast.LENGTH_SHORT).show();
            }
        });

        setValuesFromDatabaseMethod();

        datePickerMethod();

        imagePickerMethod();

        profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "Please wait for update", Toast.LENGTH_SHORT).show();
                updateInfoOnDB();
            }
        });
    }

    private void showData() {
        referenceLocation = FirebaseDatabase.getInstance().getReference("location");
        referenceLocation.child("district").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()) {
                    if(!item.getValue().toString().equals("district")) {
                        spinnerList.add(item.getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showSecondList() {
        referenceLocation.child(district).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()) {
                    spinnerList2.add(item.getValue().toString());
                }
                adapter2.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void setValuesFromDatabaseMethod() {
        if(sessionManager.getProfileImage()!="" && sessionManager.getProfileImage() != null) {
            Picasso.get().load(sessionManager.getProfileImage()).into(profileImage);
        }
        fullNameFromDB = userDetails.get(SessionManager.KEY_FULLNAME);
        profileFullNameInputLayout.getEditText().setText(fullNameFromDB);

        userNameFromDB = userDetails.get(SessionManager.KEY_USERNAME);
        profileUserNameInputLayout.getEditText().setText(userNameFromDB);

        emailFromDB = userDetails.get(SessionManager.KEY_EMAIL);
        profileEmailInputLayout.getEditText().setText(emailFromDB);

        phoneNoFromDB = userDetails.get(SessionManager.KEY_CONTACT);
        profilePhoneNoInputLayout.getEditText().setText(phoneNoFromDB);

        dateOfBirthFromDB = userDetails.get(SessionManager.KEY_DOB);
        profileDateOfBirthInputLayer.getEditText().setText(dateOfBirthFromDB);

        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                districtFromDB =  snapshot.child("district").getValue(String.class);
                subDistrictFromDB =  snapshot.child("subDistrict").getValue(String.class);
//                formDistrictAutoCom.setText(districtFromDB);
//                formSubDistrictAutoCom.setText(subDistrictFromDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addressFromDB = userDetails.get(SessionManager.KEY_ADDRESS);
        profileAddressInputLayout.getEditText().setText(addressFromDB);

        passwordFromDB = userDetails.get(SessionManager.KEY_PASSWORD);
        profilePasswordInputLayout.getEditText().setText(passwordFromDB);

        userImageFromDB = sessionManager.getProfileImage();

    }

    private void imagePickerMethod() {
        finished = false;
        final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri photoUri = result.getData().getData();
                        profileImage.setImageURI(photoUri);

                        referencePostImage = FirebaseStorage.getInstance().getReference().child(userDetails.get(SessionManager.KEY_USERNAME));
                        referencePostImage.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), userDetails.get(SessionManager.KEY_USERNAME), Toast.LENGTH_SHORT).show();
                                try {
                                    referencePostImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_SHORT).show();
                                            referenceUser.child("dp").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    userImageFromDB = uri.toString();
                                                    sessionManager.setProfileImage(uri.toString());
                                                    //Toast.makeText(getApplicationContext(), sessionManager.getProfileImage(), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getApplicationContext(), "profile image uploaded", Toast.LENGTH_SHORT).show();
                                                    finished = true;
                                                }
                                            });
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                }
        );
        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });
    }

    private void updateInfoOnDB() {
        if (isUserNameChanged()) {
//            profileProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Username can't be changed", Toast.LENGTH_SHORT).show();
            return;
        }

        isAnyValueChanged = false;

        changeFullName();
        changeEmail();
        changePhoneNo();
        changeDateOfBirth();
        changeAddress();
        changeDistrict();
        changeSubDistrict();
        changePassword();

        if (isAnyValueChanged) {
            finished = false;

            sessionManager.createLoginSession(userImageFromDB, fullNameFromET, userNameFromET, emailFromET,
                    phoneNoFromET, dateOfBirthFromET, addressFromET, passwordFromET);

            Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show();

            finished = true;

        } else {
//            profileProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Data weren't changed to be updated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUserNameChanged() {
        userNameFromET = profileUserNameInputLayout.getEditText().getText().toString();
        //Toast.makeText(this, userNameFromDB + " " + userNameFromET, Toast.LENGTH_SHORT).show();
        if (!userNameFromDB.equals(userNameFromET)) {
            return true;
        } else {
            return false;
        }
    }

    public void changeFullName() {
        fullNameFromET = profileFullNameInputLayout.getEditText().getText().toString();
        if (!fullNameFromDB.equals(fullNameFromET)) {
            reference.child(userNameGlobal).child("fullName").setValue(profileFullNameInputLayout.getEditText().getText().toString());
            isAnyValueChanged = true;
        }
    }

    private void changeEmail() {
        emailFromET = profileEmailInputLayout.getEditText().getText().toString();
        if (!emailFromDB.equals(emailFromET)) {
            reference.child(userNameGlobal).child("email").setValue(profileEmailInputLayout.getEditText().getText().toString());
            isAnyValueChanged = true;
        }
    }

    private void changePhoneNo() {
        phoneNoFromET = profilePhoneNoInputLayout.getEditText().getText().toString();
        if (!phoneNoFromDB.equals(phoneNoFromET)) {
            reference.child(userNameGlobal).child("phoneNo").setValue(profilePhoneNoInputLayout.getEditText().getText().toString());
            isAnyValueChanged = true;
        }
    }

    private void changeDateOfBirth() {
        dateOfBirthFromET = profileDateOfBirthInputLayer.getEditText().getText().toString();
        if (!dateOfBirthFromDB.equals(dateOfBirthFromET)) {
            reference.child(userNameGlobal).child("dateOfBirth").setValue(profileDateOfBirthInputLayer.getEditText().getText().toString());
            isAnyValueChanged = true;
        }
    }

    private void changeDistrict() {
        districtFromET = formDistrictAutoCom.getText().toString();
        if (!districtFromET.isEmpty() && !districtFromDB.equals(districtFromET)) {
            reference.child(userNameGlobal).child("district").setValue(districtFromET);
            isAnyValueChanged = true;
        }
    }

    private void changeSubDistrict() {
        subDistrictFromET = formSubDistrictAutoCom.getText().toString();
        if (!subDistrictFromET.isEmpty() && !subDistrictFromDB.equals(subDistrictFromET)) {
            reference.child(userNameGlobal).child("subDistrict").setValue(subDistrictFromET);
            isAnyValueChanged = true;
        }
    }


    private void changeAddress() {
        addressFromET = profileAddressInputLayout.getEditText().getText().toString();
        if (!addressFromDB.equals(addressFromET)) {
            reference.child(userNameGlobal).child("address").setValue(profileAddressInputLayout.getEditText().getText().toString());
            isAnyValueChanged = true;
        }
    }

    private void changePassword() {
        passwordFromET = profilePasswordInputLayout.getEditText().getText().toString();
        if (!passwordFromDB.equals(passwordFromET)) {
            reference.child(userNameGlobal).child("password").setValue(profilePasswordInputLayout.getEditText().getText().toString());
            isAnyValueChanged = true;
        }
    }


    public void datePickerMethod() {
        try {
            updateProfileButton = findViewById(R.id.updateProfileButtonId);
            profileDateOfBirthInputLayer = findViewById(R.id.profileDateOfBirthInputLayerId);
            dateOfBirthEditText = findViewById(R.id.profileDateOfBirthEditTextId);
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });
            setListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    i1 += 1;
                    String s = "" + i2 + "/" + i1 + "/" + i + "";
                    profileDateOfBirthInputLayer.getEditText().setText(s);
                }
            };

        } catch (Exception e) {
            Toast.makeText(EditProfile.this, "Stopped", Toast.LENGTH_SHORT).show();
        }
    }

}