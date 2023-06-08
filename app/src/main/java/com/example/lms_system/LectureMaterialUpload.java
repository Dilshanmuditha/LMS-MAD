package com.example.lms_system;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LectureMaterialUpload extends AppCompatActivity {

    private static final int PDF_PICK_CODE = 1000;
    private Uri pdfUri = null;
    private ProgressDialog progressDialog;
    private ArrayList<Uri> pdfList = new ArrayList<>();
    private ArrayList<String> moduleNameArrayList, moduleIdArrayList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference, materialsRef;
    private long timestamp;
    private TextView moduleNameTv, uploadBtn, text;
    private EditText moduleEt;
    private ImageButton backBtn, attachBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_material_upload);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("lecture_modules");
        materialsRef = database.getReference().child("ModuleMaterials");
        storage = FirebaseStorage.getInstance();

        loadModuleNames();

        backBtn = findViewById(R.id.backBtn);
        attachBtn = findViewById(R.id.attachBtn);
        moduleNameTv = findViewById(R.id.moduleNameTv);
        uploadBtn = findViewById(R.id.uploadBtn);
        moduleEt = findViewById(R.id.moduleEt);
        text = findViewById(R.id.text);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });

        moduleNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleNamePickDialog();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Internet_Check.isConnected(LectureMaterialUpload.this)) {
                    Internet_Check.showCustomDialog(LectureMaterialUpload.this);
                } else {
                    validateData();
                }
            }
        });
    }

    private String lectureTitle = "", moduleDescription = "", lectureDate = "";

    private void validateData() {
        lectureTitle = moduleEt.getText().toString().trim();

        if (TextUtils.isEmpty(lectureTitle)) {
            moduleEt.setError("Enter title");
        } else if (TextUtils.isEmpty(selectedModuleTitle)) {
            Toast.makeText(this, "Select module name...", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Select PDF...", Toast.LENGTH_SHORT).show();
        } else {
            saveLectureMaterials();
        }
    }

    private void saveLectureMaterials() {
        progressDialog.setMessage("Saving lecture materials....");
        String uid = mAuth.getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "ModuleMaterials/" + selectedModuleTitle + "/" + timestamp;

        StorageReference storageReference = storage.getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedPdfUrl = "" + uriTask.getResult();

                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("moduleId", selectedModuleId);
                            hashMap.put("moduleName", selectedModuleTitle);
                            hashMap.put("lectureTitle", lectureTitle);
                            hashMap.put("lectureUrl", uploadedPdfUrl);
                            hashMap.put("timestamp", timestamp);

                            DatabaseReference ref = database.getReference("ModuleMaterials");
                            ref.child(selectedModuleId).child(timestamp)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(LectureMaterialUpload.this, "Lecture materials uploaded successfully", Toast.LENGTH_SHORT).show();
                                            clearFields();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(LectureMaterialUpload.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LectureMaterialUpload.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        moduleEt.setText("");
        moduleNameTv.setText("");
        pdfUri = null;
        selectedModuleId = "";
        selectedModuleTitle = "";
    }

    private String selectedModuleId = "", selectedModuleTitle = "";

    private void moduleNamePickDialog() {
        String[] moduleNames = moduleNameArrayList.toArray(new String[moduleNameArrayList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Module Name")
                .setItems(moduleNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedModuleTitle = moduleNames[which];
                        moduleNameTv.setText(selectedModuleTitle);

                        int index = moduleNameArrayList.indexOf(selectedModuleTitle);
                        selectedModuleId = moduleIdArrayList.get(index);
                    }
                })
                .show();
    }

    private void pdfPickIntent() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            if (pdfUri != null) {
                Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadModuleNames() {
        moduleIdArrayList = new ArrayList<>();
        moduleNameArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                moduleIdArrayList.clear();
                moduleNameArrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String moduleName = ds.child("moduleName").getValue(String.class);

                    moduleNameArrayList.add(moduleName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LectureMaterialUpload.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
