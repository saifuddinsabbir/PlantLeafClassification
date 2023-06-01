package com.example.plantleafclassification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;
    HashMap<String, Boolean> postsLike = new HashMap<String, Boolean>();
    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String userName = mData.get(position).getUserId();

        sessionManager = new SessionManager(mContext.getApplicationContext());
        userDetails = sessionManager.getUsersDetailFromSession();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("userName").equalTo(userName);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullNameFromDB = snapshot.child(userName).child("fullName").getValue(String.class);
                holder.fullName.setText(fullNameFromDB);
                String imageFromDB = snapshot.child(userName).child("dp").getValue(String.class);
                if (imageFromDB != null && imageFromDB != "") {
                    Picasso.get().load(imageFromDB).into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.userName.setText("@" + userName);
        holder.postRating.setRating(mData.get(position).getMyRating());
        holder.time.setText(timestampToString((Long) mData.get(position).getTimeStamp()));
        holder.postDesc.setText(mData.get(position).getDescription());
        Picasso.get().load(mData.get(position).getPostImage()).into(holder.postImage);

        DatabaseReference referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(mData.get(position).getPostKey());
        referenceLikesFetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String userNameThis = snap.child("userName").getValue(String.class);
                    Boolean d = snap.child("liked").getValue(Boolean.class);

                    if (userNameThis.equals(userDetails.get(SessionManager.KEY_USERNAME))) {
                        postsLike.put(mData.get(position).getPostKey(), d);

                        if(d) {
                            holder.postLikeButton.setImageResource(R.drawable.loved3);
                        } else {
                            holder.postLikeButton.setImageResource(R.drawable.love11);
                        }
                    }

                    if (d) {
                        count++;
                    }
                }
                holder.postLikesCount.setText(count + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference referenceCommentsAdd = FirebaseDatabase.getInstance().getReference("comments").child(mData.get(position).getPostKey());
        referenceCommentsAdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    count++;
                }
                holder.postCommentsCount.setText(count + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView fullName;
        TextView userName;
        RatingBar postRating;
        TextView time;
        TextView postDesc;
        ImageView postImage;
        TextView postLikesCount;
        TextView postCommentsCount;
        ImageView postLikeButton;
        Button postCardCommentButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.postCardUserImage);
            fullName = itemView.findViewById(R.id.postCardFullName);
            userName = itemView.findViewById(R.id.postCardUserNameId);
            time = itemView.findViewById(R.id.postCardTimestampId);
            postDesc = itemView.findViewById(R.id.postCardDescriptionId);
            postImage = itemView.findViewById(R.id.postListImageId);
            postRating = itemView.findViewById(R.id.postCardRatingBarId);
            postLikesCount = itemView.findViewById(R.id.postPostLikeCountId);
            postCommentsCount = itemView.findViewById(R.id.postPostCommentCountId);
            postLikeButton = itemView.findViewById(R.id.postCardLikeButtonId);
            postCardCommentButton = itemView.findViewById(R.id.postCardCommentButtonId);

            postLikeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    DatabaseReference referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(mData.get(position).getPostKey());

                    if (!postsLike.containsKey(mData.get(position).getPostKey())) {
                        DatabaseReference referenceLikes = FirebaseDatabase.getInstance().getReference("likes").child(mData.get(position).getPostKey());
                        Like like = new Like(userDetails.get(SessionManager.KEY_USERNAME), true);
                        referenceLikes.child(userDetails.get(SessionManager.KEY_USERNAME)).setValue(like);
                        Toast.makeText(mContext.getApplicationContext(), "Liked", Toast.LENGTH_SHORT).show();
                    } else if (postsLike.get(mData.get(position).getPostKey()) == false) {
                        referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(mData.get(position).getPostKey());
                        referenceLikesFetch.child(userDetails.get(SessionManager.KEY_USERNAME)).child("liked").setValue(true);
                        Toast.makeText(mContext.getApplicationContext(), "Liked", Toast.LENGTH_SHORT).show();
                    } else {
                        referenceLikesFetch = FirebaseDatabase.getInstance().getReference("likes").child(mData.get(position).getPostKey());
                        referenceLikesFetch.child(userDetails.get(SessionManager.KEY_USERNAME)).child("liked").setValue(false);
                        Toast.makeText(mContext.getApplicationContext(), "Uniked", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            postCardCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    Intent intentToFeedbackDetails = new Intent(mContext, PostDetails.class);

                    intentToFeedbackDetails.putExtra("postKey", mData.get(position).getPostKey());
                    intentToFeedbackDetails.putExtra("userName", mData.get(position).getUserId());
                    intentToFeedbackDetails.putExtra("dateTime", (Long) mData.get(position).getTimeStamp());
                    intentToFeedbackDetails.putExtra("rating", mData.get(position).getMyRating() + "");
                    intentToFeedbackDetails.putExtra("desc", mData.get(position).getDescription());
                    intentToFeedbackDetails.putExtra("postImage", mData.get(position).getPostImage());
                    intentToFeedbackDetails.putExtra("likes", mData.get(position).getLikes());
                    intentToFeedbackDetails.putExtra("comments", mData.get(position).getComments());

                    mContext.startActivity(intentToFeedbackDetails);
                }
            });
        }
    }


    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;

    }
}
