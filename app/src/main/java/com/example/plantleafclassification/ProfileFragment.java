package com.example.plantleafclassification;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    TextView userNameProfileFreg, fullNameProfileFreg, emailProfileFreg, contactProfileFreg, dobProfileFreg, addressProfileFreg;

    ConstraintLayout editProfileButtonLayout, logoutButtonLayout;

    ImageView profileImage, postCardUserImage;

    DatabaseReference referenceUser;
    StorageReference referencePostImage;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(getContext());
        userDetails = sessionManager.getUsersDetailFromSession();

        postCardUserImage = fragment_view.findViewById(R.id.postCardUserImage);
        userNameProfileFreg = fragment_view.findViewById(R.id.userNameProfileFregId);
        fullNameProfileFreg = fragment_view.findViewById(R.id.fullNameProfileFregId);
        emailProfileFreg = fragment_view.findViewById(R.id.emailProfileFregId);
        contactProfileFreg = fragment_view.findViewById(R.id.contactProfileFregId);
        dobProfileFreg = fragment_view.findViewById(R.id.dobProfileFregId);
        addressProfileFreg = fragment_view.findViewById(R.id.addressProfileFregId);

        editProfileButtonLayout = fragment_view.findViewById(R.id.editProfileButtonLayoutId);
        logoutButtonLayout = fragment_view.findViewById(R.id.logoutButtonLayoutId);

        if(sessionManager.getProfileImage()!="" && sessionManager.getProfileImage() != null) {
            Picasso.get().load(sessionManager.getProfileImage()).into(postCardUserImage);
        }
        userNameProfileFreg.setText("@" + userDetails.get(SessionManager.KEY_USERNAME));
        fullNameProfileFreg.setText(userDetails.get(SessionManager.KEY_FULLNAME));
        emailProfileFreg.setText(userDetails.get(SessionManager.KEY_EMAIL));
        contactProfileFreg.setText(userDetails.get(SessionManager.KEY_CONTACT));
        dobProfileFreg.setText(userDetails.get(SessionManager.KEY_DOB));
        if(userDetails.get(SessionManager.KEY_ADDRESS) != "-" && userDetails.get(SessionManager.KEY_ADDRESS) != null) {
            addressProfileFreg.setText(userDetails.get(SessionManager.KEY_ADDRESS));
        } else {
            addressProfileFreg.setText("No Address");
        }

        editProfileButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfile.class));
            }
        });

        logoutButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUserFromSession();
                startActivity(new Intent(getContext(), Login.class));
            }
        });

        return fragment_view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(sessionManager.getProfileImage()!="" && sessionManager.getProfileImage() != null) {
            Picasso.get().load(sessionManager.getProfileImage()).into(postCardUserImage);
        }
        userNameProfileFreg.setText("@" + userDetails.get(SessionManager.KEY_USERNAME));
        fullNameProfileFreg.setText(userDetails.get(SessionManager.KEY_FULLNAME));
        emailProfileFreg.setText(userDetails.get(SessionManager.KEY_EMAIL));
        contactProfileFreg.setText(userDetails.get(SessionManager.KEY_CONTACT));
        dobProfileFreg.setText(userDetails.get(SessionManager.KEY_DOB));
        if(userDetails.get(SessionManager.KEY_ADDRESS) != "-" && userDetails.get(SessionManager.KEY_ADDRESS) != null) {
            addressProfileFreg.setText(userDetails.get(SessionManager.KEY_ADDRESS));
        } else {
            addressProfileFreg.setText("No Address");
        }
    }


    private void imagePickerMethod() {
        final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri photoUri = result.getData().getData();
                        profileImage.setImageURI(photoUri);

                        referencePostImage.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                try {
                                    referencePostImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            referenceUser.child("dp").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    sessionManager.setProfileImage(uri.toString());
                                                    //Toast.makeText(getContext(), sessionManager.getProfileImage(), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getContext(), "profile image uploaded", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
}