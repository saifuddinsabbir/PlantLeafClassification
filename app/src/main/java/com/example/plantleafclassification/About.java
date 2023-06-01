package com.example.plantleafclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class About extends AppCompatActivity {
    TextView nameText;
    private EditText yourNameEditText;
    private Button submitButton;

    DatabaseReference nameReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view1);
            getLifecycle().addObserver(youTubePlayerView);

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo("KzxYTWjEgaY", 0);
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
        }


    }

    private void storeNameOnDatabase() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        nameReference = FirebaseDatabase.getInstance().getReference("name");

        String name = yourNameEditText.getText().toString();

        nameReference.child("uname").setValue(name);

        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameFromDB = snapshot.child("uname").getValue(String.class);
                nameText.setText(nameFromDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}