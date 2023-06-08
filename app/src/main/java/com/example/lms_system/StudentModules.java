package com.example.lms_system;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentModules extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<ModuleData> moduleDataArrayList;

    ModuleAdapter moduleAdapter;
    FirebaseFirestore db;
    FloatingActionButton cutbtn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_modules);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        recyclerView = findViewById(R.id.moduleRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        moduleDataArrayList = new ArrayList<ModuleData>();
        moduleAdapter = new ModuleAdapter(StudentModules.this,moduleDataArrayList);

        recyclerView.setAdapter(moduleAdapter);

        cutbtn = findViewById(R.id.backbtn1);
        cutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EventChangeListner();
    }

    private void EventChangeListner() {

        db.collection("lecture_modules")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error",error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                moduleDataArrayList.add(dc.getDocument().toObject(ModuleData.class));
                            }

                            moduleAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });

    }
}