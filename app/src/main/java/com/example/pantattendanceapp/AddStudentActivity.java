package com.example.pantattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private Button addBtn;
    private EditText studentName,studentID;
    private ListView listView;
    private List<String> courseName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        db = FirebaseFirestore.getInstance();
        addBtn = findViewById(R.id.add_Btn);
        studentName = findViewById(R.id.name_editText);
        studentID = findViewById(R.id.ID_editText);
        listView = findViewById(R.id.add_student_course_name_list_view);
        courseName = new ArrayList<>();
        courseDetails();
    }

    private void addStudentToCourse(EditText studentName, EditText studentID,String n) {
        final ProgressDialog progressDialog = new ProgressDialog(AddStudentActivity.this);
        progressDialog.setMessage("Adding Student");
        progressDialog.show();
        progressDialog.setCancelable(false);
        final String name = studentName.getText().toString().trim();
        final String ID = studentID.getText().toString().trim();
        final String cName = n;
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("ID",ID);
        db.collection("courses")
                .document(cName)
                .update("name",FieldValue.arrayUnion(name))
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("courses")
                        .document(cName)
                        .update("id",FieldValue.arrayUnion(ID))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddStudentActivity.this,"Student added",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(AddStudentActivity.this,TeacherActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddStudentActivity.this,"Error"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStudentActivity.this,"Error"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });




        /*db.collection("courses")
                .document(listView.getSelectedItem().toString().trim())
                .collection("data")
                .document("students")
                .update("name", FieldValue.arrayUnion(name))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {db.collection("courses")
                            .document(listView.getSelectedItem().toString().trim())
                            .collection("data")
                            .document("students")
                            .update("ID", FieldValue.arrayUnion(ID))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddStudentActivity.this,"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        Toast.makeText(AddStudentActivity.this,"Student Name added",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddStudentActivity.this,"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/
    }
    public void courseDetails(){
        FirebaseFirestore.getInstance().collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            courseName.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                courseName.add(document.getId());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_selectable_list_item,courseName);
                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    studentName.setVisibility(View.VISIBLE);
                                    studentID.setVisibility(View.VISIBLE);
                                    addBtn.setVisibility(View.VISIBLE);
                                    addBtn(courseName.get(i));
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
    public void addBtn(final String name){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(studentName.getText().toString().trim())) {
                    studentName.setError("Enter name");
                }
                else if (TextUtils.isEmpty(studentID.getText().toString().trim())) {
                    studentID.setError("Enter ID");
                } else {
                    addStudentToCourse(studentName, studentID,name);
                }
            }
        });
    }
}