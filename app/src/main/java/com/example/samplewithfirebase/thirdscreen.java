package com.example.samplewithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class thirdscreen extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdscreen);

        Intent intent = getIntent();
    }

    public void addDoc(View view){
        EditText editTextDoc=(EditText) findViewById(R.id.editTextDocName);
        String permaddDoc = editTextDoc.getText().toString();
        EditText editTextField=(EditText) findViewById(R.id.editTextFieldName);
        String permaddField = editTextField.getText().toString();
        EditText editTextIn=(EditText) findViewById(R.id.editTextInField);
        String permaddInField = editTextIn.getText().toString();
        Map<String, Object> field = new HashMap<>();
        field.put(permaddField, permaddInField);


        db.collection("sampleData").document(permaddDoc)
                .set(field)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("perma", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("perma", "Error writing document", e);
                    }
                });
    }
}