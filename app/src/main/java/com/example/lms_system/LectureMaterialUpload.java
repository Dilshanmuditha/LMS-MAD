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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private long timestamp;
    private TextView moduleNameTv, uploadBtn, text;
    private EditText moduleEt;
    private ImageButton backBtn, attachBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_material_upload);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
        String timestamp = String.valueOf(System.currentTimeMillis

                ());
        String filePathAndName = "ModuleMaterials/" + selectedModuleTitle + "/" + timestamp;

        StorageReference fileReference = storageReference.child(filePathAndName);
        UploadTask uploadTask = fileReference.putFile(pdfUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileReference.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUri) {
                String uploadedPdfUrl = downloadUri.toString();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("moduleId", selectedModuleId);
                hashMap.put("moduleName", selectedModuleTitle);
                hashMap.put("lectureTitle", lectureTitle);
                hashMap.put("lectureUrl", uploadedPdfUrl);
                hashMap.put("timestamp", timestamp);

                database.collection("lecture_modules")
                        .document(selectedModuleId)
                        .set(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Lecture module document saved successfully
                                // Now save lecture materials within the module document
                                database.collection("lecture_modules")
                                        .document(selectedModuleId)
                                        .collection("ModuleMaterials")
                                        .add(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LectureMaterialUpload.this, "Lecture materials uploaded successfully", Toast.LENGTH_SHORT).show();
                                                clearFields();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LectureMaterialUpload.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(LectureMaterialUpload.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LectureMaterialUpload.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        database.collection("lecture_modules")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String moduleId = documentSnapshot.getId();
                            String moduleName = documentSnapshot.getString("moduleName");

                            moduleIdArrayList.add(moduleId);
                            moduleNameArrayList.add(moduleName);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LectureMaterialUpload.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}