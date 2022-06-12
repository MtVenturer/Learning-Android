package com.example.samplewithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String EXTRA_MESSAGE = "com.example.samplewithfirebase.MESSAGE";
    public static final String QUOTE_KEY = "quote";
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/quote");
    TextView mQuoteTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQuoteTextView = (TextView) findViewById(R.id.textView2);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

        Map<String, Object> dataToSave=new HashMap<String,Object>();
        dataToSave.put(QUOTE_KEY,message);
        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("quotes","Document saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("quotes","document not saved");
            }
        });
    }
    public void fetchQuote(View view){
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String quoteText = documentSnapshot.getString(QUOTE_KEY);
                    mQuoteTextView.setText(quoteText);
                }
            }
        });
    }

    public void uploadScreen(View view){
        Intent intent = new Intent(this, photoScreen.class);
        startActivity(intent);
    }

    public void storeScreen(View view){
        Intent intent = new Intent(this,StoreImage.class);
        startActivity(intent);
    }

}