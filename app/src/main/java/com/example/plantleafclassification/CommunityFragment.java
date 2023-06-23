package com.example.plantleafclassification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunityFragment extends Fragment {

    ImageView addPostButton;

    RecyclerView postListRecycleView;

    DatabaseReference referenceUser ,referenceCommunity;

    String key = null, startKey = null;

    List<Post> postList;
    PostAdapter postAdapter;

    Button prev_btn, one, next_btn;
    int pageNumber = 1;

    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_community, container, false);

        sessionManager = new SessionManager(getContext());
        userDetails = sessionManager.getUsersDetailFromSession();

        referenceUser = FirebaseDatabase.getInstance().getReference("users");
        referenceCommunity = FirebaseDatabase.getInstance().getReference("community");

        postListRecycleView = fragment_view.findViewById(R.id.postListRecycleViewId);
        postListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        postListRecycleView.setHasFixedSize(true);

        postAdapter = new PostAdapter(getContext());
        postListRecycleView.setAdapter(postAdapter);

        loadData();

        prev_btn = fragment_view.findViewById(R.id.prev_btn);
        one = fragment_view.findViewById(R.id.one);
        next_btn = fragment_view.findViewById(R.id.next_btn);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumber++;
                loadData();
            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumber--;
                loadPrevData();
            }
        });

        addPostButton = fragment_view.findViewById(R.id.addPostButtonId);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreatePost.class));
            }
        });

        return fragment_view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        fetchPostsFromDB();
    }

    private void loadData() {
        int contentSize = 2;

//        referenceFeedbacks = FirebaseDatabase.getInstance().getReference("feedbacks");

        Query query;
        if(key == null) {
            query = referenceCommunity.orderByKey().limitToFirst(contentSize);
        } else {
            query = referenceCommunity.orderByKey().startAfter(key).limitToFirst(contentSize);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> feedbackList = new ArrayList<>();
                for(DataSnapshot feedbackSnap : snapshot.getChildren()) {
                    Post feedback = feedbackSnap.getValue(Post.class);
                    if(feedbackList.isEmpty()) {
                        startKey = feedbackSnap.getKey();
                    }
                    feedbackList.add(feedback);
                    key = feedbackSnap.getKey();
                }

                one.setText(pageNumber+"");

                if(pageNumber == 1) {
                    prev_btn.setEnabled(false);
                } else {
                    prev_btn.setEnabled(true);
                }

                if(feedbackList.size() < contentSize) {
                    next_btn.setEnabled(false);
                } else {
                    next_btn.setEnabled(true);
                }

//                Toast.makeText(Feedback.this, feedbackList.size()+" ", Toast.LENGTH_SHORT).show();
                postAdapter.setItems((ArrayList<Post>) feedbackList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadPrevData() {
        int contentSize = 2;

        Query query;
        if(key == null) {
            query = referenceCommunity.orderByKey().limitToFirst(contentSize);
        } else {
            query = referenceCommunity.orderByKey().endBefore(startKey).limitToLast(contentSize);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> feedbackList = new ArrayList<>();
                for(DataSnapshot feedbackSnap : snapshot.getChildren()) {
                    Post feedback = feedbackSnap.getValue(Post.class);
                    if(feedbackList.isEmpty()) {
                        startKey = feedbackSnap.getKey();
                    }
                    feedbackList.add(feedback);
                    key = feedbackSnap.getKey();
                }

                one.setText(pageNumber+"");

                if(pageNumber == 1) {
                    prev_btn.setEnabled(false);
                } else {
                    prev_btn.setEnabled(true);
                }

                if(feedbackList.size() < contentSize) {
                    next_btn.setEnabled(false);
                } else {
                    next_btn.setEnabled(true);
                }

                postAdapter.setItems((ArrayList<Post>) feedbackList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchPostsFromDB() {
        referenceCommunity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                Post post;
                for(DataSnapshot postSnap : snapshot.getChildren()) {
                    post = postSnap.getValue(Post.class);
                    postList.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}