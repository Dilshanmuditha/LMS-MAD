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

import org.w3c.dom.Text;

public class TeacherDashboard extends AppCompatActivity {
    private TextView textView4;
    private TextView textView3;
    private TextView materialUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        textView4 = findViewById(R.id.textView4);
        textView3 = findViewById(R.id.textView3);
        materialUpload = findViewById(R.id.materialUpload);

        materialUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMaterialUpload();
            }
        });

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

        TextView moduleText = (TextView)findViewById(R.id.textView2);
        moduleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModule();
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

    }

    private void viewMaterialUpload() {
        Intent materialView = new Intent(this,LectureMaterialUpload.class);
        startActivity(materialView);
    }

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    protected void viewModule(){
        Intent moduleView = new Intent(this,LectureModule.class);
        startActivity(moduleView);
    }
}