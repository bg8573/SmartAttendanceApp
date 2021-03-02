package com.example.pantattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText login_email, login_password;
    private TextView forget_pass, teacher, not_teacher;
    private Button login_btn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore TeacherRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forget_pass = findViewById(R.id.forgot_password_textView);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_btn);
        forget_pass = findViewById(R.id.forgot_password_textView);
        teacher = findViewById(R.id.admin_panel_link);
        not_teacher = findViewById(R.id.not_an_admin_panel_link);
        fAuth = FirebaseAuth.getInstance();
        TeacherRef = FirebaseFirestore.getInstance();
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginStudent();
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.setText("Login Teacher");
                teacher.setVisibility(View.INVISIBLE);
                not_teacher.setVisibility(View.VISIBLE);
            }
        });
        not_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.setText("Login");
                teacher.setVisibility(View.VISIBLE);
                not_teacher.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loginStudent() {
        final String email = login_email.getText().toString().trim();
        String pass = login_password.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            login_email.setError("Email is required");
            return;
        } else if (TextUtils.isEmpty(pass)) {
            login_password.setError("Password is required");
            return;
        }  else {
            if (login_btn.getText().toString().equals("Login")) {
                fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Error" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                TeacherLoginCheck();
            }
        }
    }

    private void TeacherLoginCheck() {
        final String email = login_email.getText().toString().trim();
        final String pass = login_password.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            login_email.setError("Email is required");
            return;
        } else if (TextUtils.isEmpty(pass)) {
            login_password.setError("Password is required");
            return;
        }  else {
            CollectionReference cRef = TeacherRef.collection("teachers");
            Query q1 = cRef.whereEqualTo("email", email);
            q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot ds:queryDocumentSnapshots){
                        Log.i("abx",ds.getData().toString());
                        String queryEmail = ds.getString("email");
                        String queryPass = ds.getString("pass");

                        if (queryEmail.equals(email) && queryPass.equals(pass)){
                            if (fAuth.getCurrentUser() == null) {
                                fAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        startActivity(new Intent(LoginActivity.this,TeacherActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("bbb",e.getMessage());
                                        Toast.makeText(LoginActivity.this,String.valueOf(e),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            //startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                            //return;
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Incorrect email or password",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            });


        }
    }
}