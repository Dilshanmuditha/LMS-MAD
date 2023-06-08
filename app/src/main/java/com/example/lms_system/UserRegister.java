package com.example.lms_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {


    EditText fullName,email,password,id;
    Button registerBtn;

    FloatingActionButton cutbtn;
    RadioButton studentRBtn, teacherRBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        id = findViewById(R.id.registerId);
        registerBtn = findViewById(R.id.registerBtn);
        studentRBtn = findViewById(R.id.isStudent);
        teacherRBtn = findViewById(R.id.isTeacher);

        cutbtn = findViewById(R.id.cutButton);
        cutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(id);

                if (valid){
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(UserRegister.this,"Account Created Successfull.",Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("Name",fullName.getText().toString());
                            userInfo.put("Email",email.getText().toString());
                            userInfo.put("Password",password.getText().toString());
                            userInfo.put("IdNumber",id.getText().toString());

                            if (studentRBtn.isChecked()){
                                userInfo.put("userRole","student");
                            } else if (teacherRBtn.isChecked()) {
                                userInfo.put("userRole","teacher");
                            }
                            df.set(userInfo);

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserRegister.this,"Failed to create Account.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}