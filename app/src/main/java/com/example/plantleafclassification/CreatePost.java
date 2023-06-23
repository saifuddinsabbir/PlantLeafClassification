package com.example.plantleafclassification;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;

public class CreatePost extends AppCompatActivity {

    ImageView createPostBackButton, writePostUserImage, writePostRatingFace, writePostImage;

    TextView writePostPostButton, writePostFullName, writePostUserName, writePostAverageRating, addImage;

    TextInputEditText writePostDesc;

    ProgressBar writePostProgressBar;

    RatingBar writePostRatingBar;

    Uri pickedPhotoUri;

    DatabaseReference referenceUser, referenceCommunity;
    StorageReference referenceImage;

    Post post;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    double sumOfAllrating;
    int totalNoOfRating;
    float myRating;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUsersDetailFromSession();

        referenceUser = FirebaseDatabase.getInstance().getReference("users").child(userDetails.get(SessionManager.KEY_USERNAME));
        referenceImage = FirebaseStorage.getInstance().getReference("posts").child(userDetails.get(SessionManager.KEY_USERNAME));

        //Hooks
        createPostBackButton = findViewById(R.id.createPostBackButtonId);
        writePostPostButton = findViewById(R.id.writePostPostButtonId);
        writePostUserImage = findViewById(R.id.writePostUserImage);
        writePostFullName = findViewById(R.id.writePostFullName);
        writePostUserName = findViewById(R.id.writePostUserNameId);
        writePostDesc = findViewById(R.id.writePostDescId);
        writePostProgressBar = findViewById(R.id.writePostProgressBarId);
        writePostRatingBar = findViewById(R.id.writePostRatingBarId);
        writePostRatingFace = findViewById(R.id.writePostRatingFaceId);
        writePostAverageRating = findViewById(R.id.writePostAverageRatingId);
        writePostImage = findViewById(R.id.writePostImageId);
        addImage = findViewById(R.id.addImageId);

//        if(!sessionManager.getProfileImage().equals(null)) {
//            Picasso.get().load(sessionManager.getProfileImage()).into(writePostUserImage);
//        }

        writePostProgressBar.setVisibility(View.INVISIBLE);
        writePostRatingFace.setVisibility(View.INVISIBLE);

        writePostFullName.setText(userDetails.get(SessionManager.KEY_FULLNAME));
        writePostUserName.setText("@" + userDetails.get(SessionManager.KEY_USERNAME));

        ratingActivities();

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        writePostImage.setAnimation(anim);

        pickImageForPost();

        //toolbar
        createPostBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        writePostPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePostProgressBar.setVisibility(View.VISIBLE);
                //Toast.makeText(CreatePost.this, "Clicked", Toast.LENGTH_SHORT).show();
                storePostInfoIntoDB();
            }
        });
    }

    private void ratingActivities() {
        DatabaseReference referenceFeedbacksAgain = FirebaseDatabase.getInstance().getReference("community");
        referenceFeedbacksAgain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sumOfAllrating = 0.0;
                totalNoOfRating = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    sumOfAllrating += Double.parseDouble(snap.child("myRating").getValue().toString());
                    totalNoOfRating++;
                }
                //writePostAverageRating.setText("Average rating: " + decfor.format((sumOfAllrating) / (totalNoOfRating)) + "/5.00");
                //Toast.makeText(CreatePost.this, sumOfAllrating + " " + totalNoOfRating, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            writePostRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    myRating = rating;
                    writePostRatingFace.setVisibility(View.VISIBLE);

                    if (rating <= 1) {
                        writePostRatingFace.setImageResource(R.drawable.emoji19);
                    } else if (rating <= 2) {
                        writePostRatingFace.setImageResource(R.drawable.emoji13);
                    } else if (rating <= 3) {
                        writePostRatingFace.setImageResource(R.drawable.emoji15);
                    } else if (rating <= 4) {
                        writePostRatingFace.setImageResource(R.drawable.emoji17);
                    } else {
                        writePostRatingFace.setImageResource(R.drawable.emoji20);
                    }

                    Double avererageRating = (sumOfAllrating + rating) / (totalNoOfRating + 1);
                    writePostAverageRating.setText("Average rating: " + decfor.format(avererageRating) + "/5.00");
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void pickImageForPost() {

        final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        pickedPhotoUri = result.getData().getData();
                        writePostImage.setImageURI(pickedPhotoUri);
                        addImage.setVisibility(View.INVISIBLE);
                    }
                }

        );
        writePostImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });
    }

    private void storePostImageIntoDB(String key) {
        referenceImage.child(key).putFile(pickedPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreatePost.this, "image uploaded", Toast.LENGTH_SHORT).show();
                referenceImage.child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        post.setPostImage(uri.toString());

                        referenceCommunity.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CreatePost.this, "Post Added", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreatePost.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //referenceUser.child("dp").setValue(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreatePost.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storePostInfoIntoDB() {
        String postDesc = writePostDesc.getText().toString();

        post = new Post(postDesc, myRating, "", userDetails.get(SessionManager.KEY_USERNAME), 0, 0);

        referenceCommunity = FirebaseDatabase.getInstance().getReference("community").push();
        String key = referenceCommunity.getKey();
        post.setPostKey(key);

        storePostImageIntoDB(key);

    }
}