package com.siddheswar.camtext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnDetectText;
    FloatingActionButton FABCamera;
    ImageView imgCaptured;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    TextView txtImageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(getApplicationContext());


        btnDetectText = findViewById(R.id.btnDetectText);
        FABCamera = findViewById(R.id.FABCamera);
        imgCaptured = findViewById(R.id.imgCapture);
        txtImageText = findViewById(R.id.txtImageText);



        FABCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });


        btnDetectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectText();
            }
        });


    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
            imgCaptured.setImageBitmap(imageBitmap);
        }
    }

    private void detectText() {

        FirebaseVisionImage FBVI = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextDetector fbTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        fbTextDetector.detectInImage(FBVI).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayText(firebaseVisionText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void displayText(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.Block> list = firebaseVisionText.getBlocks();
        if(list.size() ==0)
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
            {
                String text = block.getText();
                txtImageText.setText(text);

            }
        }
    }


}