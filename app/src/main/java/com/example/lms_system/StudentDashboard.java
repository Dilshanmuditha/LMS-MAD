package com.example.lms_system;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentDashboard extends AppCompatActivity {
    private TextView textView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        textView4 = findViewById(R.id.textView4);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("Name");
                        if (userName != null) {
                            // Set the user's name to the TextView
                            textView4.setText("Welcome to LMS dashboard "+userName);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting user document: " + task.getException());
                    // Handle error case
                }
            }
        });

        TextView timeText = (TextView)findViewById(R.id.textView3);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://nsbm365.sharepoint.com/:x:/g/SOC/EXBSFzIE049OiQbLi1Tjpn0BU5gKXlU12G58_HAZ_RrlMA?rtime=OvvZxDNn20g");
            }
        });

        TextView assText = (TextView)findViewById(R.id.textView1);
        assText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://nsbm365.sharepoint.com/:x:/g/SOC/EXBSFzIE049OiQbLi1Tjpn0BU5gKXlU12G58_HAZ_RrlMA?rtime=OvvZxDNn20g");
            }
        });

        TextView module = (TextView) findViewById(R.id.textView2);
        module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModule();
            }
        });
    }

    private void viewModule() {
        Intent userIntent = new Intent(this,StudentModules.class);
        startActivity(userIntent);
    }

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}