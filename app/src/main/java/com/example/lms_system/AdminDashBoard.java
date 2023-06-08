package com.example.lms_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
        Button regUser = (Button) findViewById(R.id.btnUser);
        regUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserRegistration();
            }
        });
        Button showStudent = (Button) findViewById(R.id.btnShowStudent);
        showStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStudent();
            }
        });

        Button showTeacher = (Button) findViewById(R.id.btnShowTeacher);
        showTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeacher();
            }
        });

        Button btnSchedule = (Button) findViewById(R.id.btnSchedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://nsbm365.sharepoint.com/:x:/g/SOC/EXBSFzIE049OiQbLi1Tjpn0BU5gKXlU12G58_HAZ_RrlMA?rtime=OvvZxDNn20g");
            }
        });
    }

    private void showTeacher() {
        Intent intent = new Intent(this,TeacherDetails.class);
        startActivity(intent);
    }

    private void showStudent() {
        Intent intent = new Intent(this,StudentDetails.class);
        startActivity(intent);
    }

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    protected void viewUserRegistration(){
        Intent userIntent = new Intent(this,UserRegister.class);
        startActivity(userIntent);
    }


}