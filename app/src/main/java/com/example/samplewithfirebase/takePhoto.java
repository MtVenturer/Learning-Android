package com.example.samplewithfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageRegistrar;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class takePhoto extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button bTakePicture;
    Button bEnd;
    PreviewView viewFinder;
    private ImageCapture imageCapture;
    //private VideoCapture videoCapture;
    SeekBar zoom_slider;
    SeekBar exposure_slider;
    TextView zoom_level;
    TextView exposure_level;
    Integer total_sec;
    Integer int_in_sec;
    Integer sec_left;
    Integer total_pics;
    TextView timerText;
    TextView captureCtr;
    TextView uploadCtr;
    Integer captured=0;
    Integer uploaded=0;
    String recording_name;
    Boolean cameraRunning;
//    private Handler camHandler = new Handler();
//    private Runnable cameraRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if(captured<total_pics){
//                capturePhoto();
//                captured++;
//                camHandler.postDelayed(this,1000*int_in_sec);
//                timerText.setText("Seconds Left:\n"+ String.valueOf(total_sec));
//                captureCtr.setText("Captured:\n"+String.valueOf(captured)+"/"+total_pics);
//                uploadCtr.setText("Uploaded:\n"+String.valueOf(uploaded)+"/"+captured);
//            }
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //set ui elements
        timerText=findViewById(R.id.timerText);
        captureCtr=findViewById(R.id.capture_ctr);
        uploadCtr=findViewById(R.id.upload_ctr);
        viewFinder = findViewById(R.id.viewFinder);
        bTakePicture = findViewById(R.id.image_capture_button);
        bEnd=findViewById(R.id.end_capture_btn);
        //bRecord = findViewById(R.id.video_capture_button);
        //bRecord.setText("start recording"); // Set the initial text of the button
        zoom_slider= findViewById(R.id.zoom_slider);
        zoom_level=findViewById(R.id.zoom_level);
        exposure_level=findViewById(R.id.exposure_level);
        exposure_slider=findViewById(R.id.exposure_slider);

        //get data from previous activity photoScreen
        Intent intent = getIntent();
        recording_name=intent.getStringExtra("recording_name");
        Integer total_rec_hrs=intent.getIntExtra("total_rec_hrs",0);

        Integer total_rec_min=intent.getIntExtra("total_rec_min",0);
        Log.d("recname",recording_name);
        Log.d("total_rec_hrs", String.valueOf(total_rec_hrs));
        Integer total_rec_sec=intent.getIntExtra("total_rec_sec",0);
        Integer int_hrs=intent.getIntExtra("int_hrs",0);
        Integer int_min=intent.getIntExtra("int_min",0);
        Integer int_sec=intent.getIntExtra("int_sec",0);
        Log.d("total_rec_min", String.valueOf(total_rec_hrs));
        Log.d("total_rec_sec", String.valueOf(total_rec_hrs));
        Log.d("int_hrs", String.valueOf(total_rec_hrs));
        Log.d("int_min", String.valueOf(total_rec_hrs));
        Log.d("total_rec_hrs", String.valueOf(total_rec_hrs));

        //convert total recording time to seconds
        total_sec = total_rec_sec+total_rec_min*60+total_rec_hrs*3600;

        //convert total interval time to sec
        int_in_sec = int_sec+int_min*60+int_hrs*3600;
        sec_left = total_sec;
        total_pics = total_sec/int_in_sec;
        timerText.setText("Seconds Left:\n"+ String.valueOf(total_sec));
        captureCtr.setText("Captured:\n"+String.valueOf(captured)+"/"+total_pics);
        uploadCtr.setText("Uploaded:\n"+String.valueOf(uploaded)+"/"+captured);
        Log.d("total_sec_calculation", String.valueOf(total_sec));
        Log.d("interval in seconds", String.valueOf(int_in_sec));
        Log.d("total # pics",String.valueOf(total_pics));
        setTitle(recording_name);
        CountDownTimer timer = new CountDownTimer(1000*sec_left, 1000) {

            public void onTick(long millisUntilFinished) {
                sec_left--;
                timerText.setText("Seconds Left:\n"+ sec_left+"/"+String.valueOf(total_sec));
                if((total_sec-sec_left)%int_in_sec==0){
                    capturePhoto();
                    captured++;
                    captureCtr.setText("Captured:\n"+String.valueOf(captured)+"/"+total_pics);
                    uploadCtr.setText("Uploaded:\n"+String.valueOf(uploaded)+"/"+captured);
                };

            }

            public void onFinish() {
                timerText.setText("Done!");
            }
        };



        //start capture button
        bTakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button


               // cameraRunnable.run();
                timer.start();
            }
        });

        //stop capture button
        bEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
//                camHandler.removeCallbacks(cameraRunnable);
            }
        });


//        bRecord.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Code here executes on main thread after user presses button
//                recordVideo();
//            }
//        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());


    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Video capture use case
//        videoCapture = new VideoCapture.Builder()
//                .setVideoFrameRate(30)
//                .build();

        // Image analysis use case
//        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//
//        imageAnalysis.setAnalyzer(getExecutor(), (ImageAnalysis.Analyzer) this);

        //bind to lifecycle:
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
        CameraControl cameraControl = camera.getCameraControl();
        CameraInfo cameraInfo = camera.getCameraInfo();
        exposure_level.setText("Exp: "+cameraInfo.getExposureState().getExposureCompensationIndex());
        zoom_level.setText("Zoom: "+cameraInfo.getZoomState().getValue().getLinearZoom());

        zoom_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cameraControl.setLinearZoom((float)progress/10);
                zoom_level.setText("Zoom: "+cameraInfo.getZoomState().getValue().getLinearZoom());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        exposure_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cameraControl.setExposureCompensationIndex(progress);
                exposure_level.setText("Exp: "+cameraInfo.getExposureState().getExposureCompensationIndex());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void analyze(@NonNull ImageProxy image) {
        // image processing here for the current frame
        Log.d("TAG", "analyze: got the frame at: " + image.getImageInfo().getTimestamp());
        image.close();
    }

    private void capturePhoto() {

        StorageReference imgref = storageReference.child(recording_name).child(String.valueOf(captured));


        long timestamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, String.valueOf(uploadCtr));
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");


        //OutputStream imgout = new ByteArrayOutputStream(1024);
//        File imgfile = new File()

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),

                new ImageCapture.OnImageSavedCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(takePhoto.this, "Photo has been saved successfully.", Toast.LENGTH_SHORT).show();
                        UploadTask uploadTask = imgref.putFile(outputFileResults.getSavedUri());
                        ContentResolver deleteimg = getContentResolver();
                        deleteimg.delete(outputFileResults.getSavedUri(), null);
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                File file = new File(outputFileResults.getSavedUri().getPath());
                                file.delete();
                            }
                        });

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(takePhoto.this, "Error saving photo: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );



//        ByteArrayOutputStream buffer = (ByteArrayOutputStream) imgout;
//        byte[] bytes = buffer.toByteArray();
//        InputStream imgstream = new ByteArrayInputStream(bytes);
//        UploadTask uploadTask = imgref.putStream(imgstream);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//            }
//        });


    }



}



//    @SuppressLint("RestrictedApi")
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.image_capture_button:
//                capturePhoto();
//                break;
//            case R.id.video_capture_button:
//                if (bRecord.getText() == "start recording") {
//                    bRecord.setText("stop recording");
//                    recordVideo();
//                } else {
//                    bRecord.setText("start recording");
//                    videoCapture.stopRecording();
//                }
//                break;
//
//        }
//    }


//    @SuppressLint("RestrictedApi")
//    private void recordVideo() {
//        if (videoCapture != null) {
//
//            long timestamp = System.currentTimeMillis();
//
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
//
//            try {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                videoCapture.startRecording(
//                        new VideoCapture.OutputFileOptions.Builder(
//                                getContentResolver(),
//                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                                contentValues
//                        ).build(),
//                        getExecutor(),
//                        new VideoCapture.OnVideoSavedCallback() {
//                            @Override
//                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
//                                Toast.makeText(takePhoto.this, "Video has been saved successfully.", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                                Toast.makeText(takePhoto.this, "Error saving video: " + message, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                );
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
