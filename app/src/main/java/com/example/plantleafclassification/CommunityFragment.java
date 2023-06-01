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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunityFragment extends Fragment {

    ImageView addPostButton;

    RecyclerView postListRecycleView;

    DatabaseReference referenceUser ,referenceCommunity;

    List<Post> postList;
    PostAdapter postAdapter;

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
        fetchPostsFromDB();
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

                //post = new Post("", 0, "", "", 0, 0);
                //postList.add(post);



                postAdapter = new PostAdapter(getContext(), postList);
                postListRecycleView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}