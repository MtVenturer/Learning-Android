package com.example.samplewithfirebase;

import androidx.appcompat.app.AlertDialog;
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
    EditText total_rec_hrs_box;
    EditText total_rec_min_box;
    EditText total_rec_sec_box;
    EditText int_hr_box;
    EditText int_min_box;
    EditText int_sec_box;
    Button takePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //link components by id
        setContentView(R.layout.activity_photo_screen);
        recording_name_box=findViewById(R.id.recording_name);
        total_rec_hrs_box=findViewById(R.id.edit_recording_hr);
        total_rec_min_box=findViewById(R.id.edit_recording_mins);
        total_rec_sec_box=findViewById(R.id.edit_recording_sec);
        int_hr_box=findViewById(R.id.edit_interval_hr);
        int_min_box=findViewById(R.id.edit_interval_min);
        int_sec_box=findViewById(R.id.edit_interval_sec);

        takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String recording_name=recording_name_box.getText().toString();
                Intent intent = new Intent(getApplicationContext(), takePhoto.class);
                //send timing data to next activity
                String total_rec_hrs=total_rec_hrs_box.getText().toString();
                String total_rec_min=total_rec_min_box.getText().toString();
                String total_rec_sec=total_rec_sec_box.getText().toString();
                String int_hrs=total_rec_hrs_box.getText().toString();
                String int_min=total_rec_hrs_box.getText().toString();
                String int_sec=total_rec_hrs_box.getText().toString();

                Integer total_rec_hrs_num;
                Integer total_rec_min_num;
                Integer total_rec_sec_num;
                Integer int_hrs_num;
                Integer int_min_num;
                Integer int_sec_num;

                if (total_rec_hrs.isEmpty()){
                    total_rec_hrs_num=0;
                }else{
                    total_rec_hrs_num=Integer.parseInt(total_rec_hrs);
                }
                intent.putExtra("total_rec_hrs",total_rec_hrs_num);

                if (total_rec_min.isEmpty()){
                    total_rec_min_num=0;
                }else{
                    total_rec_min_num=Integer.parseInt(total_rec_min);
                }
                intent.putExtra("total_rec_min",total_rec_min_num);

                if (total_rec_sec.isEmpty()){
                    total_rec_sec_num=0;
                }else{
                    total_rec_sec_num=Integer.parseInt(total_rec_sec);
                }
                intent.putExtra("total_rec_sec",0);

                if (int_hrs.isEmpty()){
                    int_hrs_num=0;
                }else{
                    int_hrs_num=Integer.parseInt(int_hrs);
                }
                intent.putExtra("int_hr",int_hrs_num);

                if (int_min.isEmpty()){
                    int_min_num=0;
                }else{
                    int_min_num=Integer.parseInt(int_min);
                }
                intent.putExtra("int_min",int_min_num);

                if (int_sec.isEmpty()){
                    int_sec_num=0;
                }else{
                    int_sec_num=Integer.parseInt(int_sec);
                }
                intent.putExtra("int_sec",int_sec_num);
                //convert total recording time to seconds
                Integer total_sec = total_rec_sec_num+total_rec_min_num*60+total_rec_hrs_num*3600;
                //convert total interval time to sec
                Integer int_in_sec = int_sec_num+int_min_num*60+int_hrs_num*3600;
                //popup dialog if fields are invalid
                if(total_sec==0 || int_in_sec==0){
                    // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(photoScreen.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("error")
                            .setTitle("invalid input");

                    // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                    AlertDialog dialog = builder.create();
                }else{
                    intent.putExtra("recording_name",recording_name);
                    Log.d("name",recording_name);
                    startActivity(intent);
                }


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
