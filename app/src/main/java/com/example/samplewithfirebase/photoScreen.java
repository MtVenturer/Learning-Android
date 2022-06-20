package com.example.samplewithfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class photoScreen extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    //initialize all components
    EditText recording_name_box;
    EditText total_rec_hrs;
    EditText total_rec_min;
    EditText total_rec_sec;
    EditText int_hr;
    EditText int_min;
    EditText int_sec;
    Button takePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //link components by id
        setContentView(R.layout.activity_photo_screen);
        recording_name_box=findViewById(R.id.recording_name);
        total_rec_hrs=findViewById(R.id.edit_recording_hr);
        total_rec_min=findViewById(R.id.edit_recording_mins);
        total_rec_sec=findViewById(R.id.edit_recording_sec);
        int_hr=findViewById(R.id.edit_interval_hr);
        int_min=findViewById(R.id.edit_interval_min);
        int_sec=findViewById(R.id.edit_interval_sec);

        takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String recording_name=recording_name_box.getText().toString();
                Intent intent = new Intent(getApplicationContext(), takePhoto.class);
                //send timing data to next activity
                intent.putExtra("total_rec_hrs",Integer.parseInt(total_rec_hrs.getText().toString()));
                intent.putExtra("total_rec_min",Integer.parseInt(total_rec_min.getText().toString()));
                intent.putExtra("total_rec_sec",Integer.parseInt(total_rec_sec.getText().toString()));
                intent.putExtra("int_hr",Integer.parseInt(int_hr.getText().toString()));
                intent.putExtra("int_min",Integer.parseInt(int_min.getText().toString()));
                intent.putExtra("int_sec",Integer.parseInt(int_sec.getText().toString()));
                intent.putExtra("recording_name",recording_name);
                Log.d("name",recording_name);
                startActivity(intent);
            }
        });
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }



}
