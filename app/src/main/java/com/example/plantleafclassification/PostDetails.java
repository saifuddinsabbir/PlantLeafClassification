package com.example.plantleafclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {

    ImageView postBackButton, feedbackPostUserImage, feedbackCommentUserImage, postListImage;
    TextView postHeading, feedbackPostFullName, feedbackPostUserName, feedbackPostTimestamp, feedbackPostExperience,
            feedbackPostTitle, feedbackPostDescription, feedbackPostLikeCount, feedbackPostCommentCount;
    EditText feedbackCommentEditText;
    ImageView feedbackPostLikeButton;
    Button feedbackPostCommentButton, feedbackPostShareButton;
    ImageButton feedbackCommentAddButton;
    RatingBar feedbackPostRatingBar;

    RecyclerView recycleViewComment;

    List<Like> listLike;
    List<Comment> listComment;

    CommentAdapter commentAdapter;

    DatabaseReference referenceFeedbacks, referenceUsers, referenceComments, referenceCommentsAdd, referenceLikes,
            referenceLikesFetch;
    Query checkFeedbackPost, checkPostUser, checkCommentUser;
    String postKeyGlobal, userNameGlobal;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;


    String postUserImageFromDB, postFullNameFromDB, postUserNameFromDB, dateTimeFromDB, ratingFromDB, postTitleFromDB,
            postDescFromDB, postImageFromDB, commentUserImage, commentFullName, likeCountFromDB, commentCountFromDB;

    Boolean userOnceLiked = false, userLiked = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUsersDetailFromSession();

        //connect to database
        referenceFeedbacks = FirebaseDatabase.getInstance().getReference("feedbacks");
        //checkFeedbackPost = referenceFeedbacks.orderByChild("postKey").equalTo(postKeyGlobal);

        postBackButton = findViewById(R.id.postBackButtonId);
        postHeading= findViewById(R.id.postHeadingId);
        feedbackPostUserImage = findViewById(R.id.postCardUserImage);
        feedbackPostFullName = findViewById(R.id.postCardFullName);
        feedbackPostUserName = findViewById(R.id.postCardUserNameId);
        feedbackPostTimestamp = findViewById(R.id.postCardTimestampId);
        feedbackPostRatingBar = findViewById(R.id.postCardRatingBarId);
        feedbackPostDescription = findViewById(R.id.postCardDescriptionId);
        postListImage = findViewById(R.id.postListImageId);
        feedbackPostLikeCount = findViewById(R.id.postPostLikeCountId);
        feedbackPostCommentCount = findViewById(R.id.postPostCommentCountId);
        feedbackPostLikeButton = findViewById(R.id.postCardLikeButtonId);
        feedbackPostCommentButton = findViewById(R.id.postCardCommentButtonId);
        feedbackCommentUserImage = findViewById(R.id.feedbackCommentUserImageId);
        feedbackCommentEditText = findViewById(R.id.feedbackCommentEditTextId);
        feedbackCommentAddButton = findViewById(R.id.feedbackCommentAddButtonId);

        recycleViewComment = findViewById(R.id.recycleViewCommentId);

        setDataFromExtras();

        postBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        feedbackPostLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeLikeOnDatabase();
            }
        });

        feedbackCommentAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackCommentAddButton.setVisibility(View.INVISIBLE);
                storeCommentOnDatabase();
            }
        });
    }

    private void setDataFromExtras() {
        userNameGlobal = userDetails.get(SessionManager.KEY_USERNAME);
        postKeyGlobal = getIntent().getStringExtra("postKey");
        postUserNameFromDB = getIntent().getStringExtra("userName");
        dateTimeFromDB = timestampToString(getIntent().getExtras().getLong("dateTime"));
        ratingFromDB = getIntent().getStringExtra("rating");
        postDescFromDB = getIntent().getStringExtra("desc");
        postImageFromDB = getIntent().getStringExtra("postImage");
        likeCountFromDB = getIntent().getStringExtra("likes");
        commentCountFromDB = getIntent().getStringExtra("comments");

        referenceUsers = FirebaseDatabase.getInstance().getReference("users");
        checkPostUser = referenceUsers.orderByChild("userName").equalTo(postUserNameFromDB);
        checkPostUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postUserImageFromDB = snapshot.child(postUserNameFromDB).child("dp").getValue(String.class);
                Picasso.get().load(postUserImageFromDB).into(feedbackPostUserImage);
                postFullNameFromDB = snapshot.child(postUserNameFromDB).child("fullName").getValue(String.class);
                feedbackPostFullName.setText(postFullNameFromDB);
                postHeading.setText(postFullNameFromDB + "'s Post");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Picasso.get().load(postImageFromDB).into(postListImage);

        feedbackPostUserName.setText("@" + postUserNameFromDB);
        feedbackPostTimestamp.setText(dateTimeFromDB);
        feedbackPostRatingBar.setRating(Float.parseFloat(ratingFromDB));

        feedbackPostDescription.setText(postDescFromDB);

        Picasso.get().load(sessionManager.getProfileImage()).into(feedbackCommentUserImage);
        commentFullName = userDetails.get(SessionManager.KEY_FULLNAME);

        fetchlikes();
        fetchComments();
    }

    private void fetchlikes() {
        referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(postKeyGlobal);
        referenceLikesFetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String userNameThis = snap.child("userName").getValue(String.class);
                    Boolean d = snap.child("liked").getValue(Boolean.class);

                    if (userNameThis.equals(userNameGlobal)) {
                        userOnceLiked = true;
                        userLiked = d;
                        if(d) {
                            feedbackPostLikeButton.setImageResource(R.drawable.loved3);
                        } else {
                            feedbackPostLikeButton.setImageResource(R.drawable.love11);
                        }
                    }

                    if (d) {
                        count++;
                    }
//                    Toast.makeText(FeedbackDetails.this, userNameThis + " " + d, Toast.LENGTH_SHORT).show();
                }

                feedbackPostLikeCount.setText(count + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchComments() {

        recycleViewComment.setLayoutManager(new LinearLayoutManager(this));

        referenceCommentsAdd = FirebaseDatabase.getInstance().getReference("comments").child(postKeyGlobal);
        referenceCommentsAdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);
                }

                commentAdapter = new CommentAdapter(PostDetails.this, listComment);
                recycleViewComment.setAdapter(commentAdapter);

                feedbackPostCommentCount.setText(listComment.size() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void storeLikeOnDatabase() {

        if (userLiked) {
            referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(postKeyGlobal);
            referenceLikesFetch.child(userNameGlobal).child("liked").setValue(false);
            feedbackPostLikeButton.setImageResource(R.drawable.love11);
            Toast.makeText(this, "Uniked", Toast.LENGTH_SHORT).show();
        } else if (userOnceLiked) {
            referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(postKeyGlobal);
            referenceLikesFetch.child(userNameGlobal).child("liked").setValue(true);
            feedbackPostLikeButton.setImageResource(R.drawable.loved3);
            Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
        } else {
            referenceLikes = FirebaseDatabase.getInstance().getReference("likes").child(postKeyGlobal);
            Like like = new Like(userNameGlobal, true);
            referenceLikes.child(userNameGlobal).setValue(like);
            feedbackPostLikeButton.setImageResource(R.drawable.loved3);
            Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeCommentOnDatabase() {
        String userImage = sessionManager.getProfileImage();
        String fullName = userDetails.get(SessionManager.KEY_FULLNAME);
        String commentContent = feedbackCommentEditText.getText().toString();

        if (!commentContent.isEmpty()) {
            referenceComments = FirebaseDatabase.getInstance().getReference("comments").child(postKeyGlobal).push();

            Comment comment = new Comment(userImage, fullName, userDetails.get(SessionManager.KEY_USERNAME), commentContent);

            referenceComments.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT).show();
                    feedbackCommentEditText.setText("");
                    feedbackCommentAddButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            feedbackCommentAddButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Empty comment", Toast.LENGTH_SHORT).show();
        }
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}