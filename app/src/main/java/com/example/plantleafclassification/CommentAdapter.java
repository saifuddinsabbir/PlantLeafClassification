package com.example.plantleafclassification;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Picasso.get().load(mData.get(position).getUserImage()).into(holder.userImage);
        holder.fullName.setText(mData.get(position).getFullName());
        holder.userName.setText("@" + mData.get(position).getUserName());
        holder.dateTime.setText(timestampToString((Long) mData.get(position).getTimestamp()));
        holder.commentContent.setText(mData.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView fullName, userName, dateTime, commentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.commentUserImage);
            fullName = itemView.findViewById(R.id.commentFullName);
            userName = itemView.findViewById(R.id.commentUserNameId);
            dateTime = itemView.findViewById(R.id.commentTimestampId);
            commentContent = itemView.findViewById(R.id.commentDescriptionId);

        }
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", calendar).toString();
        return date;


    }
}

