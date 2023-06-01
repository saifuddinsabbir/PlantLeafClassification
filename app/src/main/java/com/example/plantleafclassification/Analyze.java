package com.example.plantleafclassification;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.plantleafclassification.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.time.temporal.ValueRange;
import java.util.List;

public class Analyze extends AppCompatActivity {
    public static final int CAMERA_ACTION_CODE = 1;
    private ImageView photo;
    private Button gallery;
    Bitmap bitmap;
    LottieAnimationView searchingLottieAnimation;
    TextView analyzeClassText, analyzeProbabilityText;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        analyzeClassText = findViewById(R.id.analyzeClassTextId);
        analyzeProbabilityText = findViewById(R.id.analyzeProbabilityTextId);
        searchingLottieAnimation = findViewById(R.id.searchingLottieAnimationId);

        photo = (ImageView) findViewById(R.id.leafcam);
        gallery = findViewById(R.id.gallery_btn);


//        //setting animation elementgf
//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(1500); //You can manage the blinking time with this parameter
//        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        photo.setAnimation(anim);
//        //end animation


        //Take a picture
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    bitmap = (Bitmap) bundle.get("data");
                    Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    searchingLottieAnimation.setVisibility(View.INVISIBLE);
                    photo.setVisibility(View.VISIBLE);
                    photo.setImageBitmap(bitmap);
                    bitmap = bmp;

                    predictClass();
                }
            }
        });
        searchingLottieAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(Analyze.this, "There is no app that support this action",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Load image from gallery
        final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri photoUri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        searchingLottieAnimation.setVisibility(View.INVISIBLE);
                        photo.setVisibility(View.VISIBLE);
                        photo.setImageURI(photoUri);

                        predictClass();
                    }
                }
        );
        gallery.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });
    }

    private void predictClass() {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            double maxProbability = 0.0;
            int maxProbabilityIndex = 0;
            for (int i = 0; i < probability.size(); i++) {
                if (maxProbability <= probability.get(i).getScore()) {
                    maxProbability = probability.get(i).getScore();
                    maxProbabilityIndex = i;
                }
            }

            String maxProbabilityClass = "State: " + probability.get(maxProbabilityIndex).getLabel();
            String maxProbabilityString = "Probability: " + probability.get(maxProbabilityIndex).getScore();

            analyzeClassText.setText(maxProbabilityClass);
            analyzeProbabilityText.setText( maxProbabilityString);
            Toast.makeText(getApplicationContext(), maxProbabilityClass, Toast.LENGTH_SHORT).show();
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
}
