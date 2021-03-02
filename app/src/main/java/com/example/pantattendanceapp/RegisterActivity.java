package com.example.pantattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText register_name,register_ID,register_email,register_password;
    private Button create_account_btn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore TeacherRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_name = findViewById(R.id.register_name);
        register_ID = findViewById(R.id.register_ID);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        create_account_btn = findViewById(R.id.register_btn);
        fAuth = FirebaseAuth.getInstance();
        TeacherRef = FirebaseFirestore.getInstance();
        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        final String name = register_name.getText().toString().trim();
        final String email = register_email.getText().toString().trim();
        final String ID = register_ID.getText().toString().trim();
        String pass = register_password.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            register_name.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(ID)) {
            register_name.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            register_email.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            register_password.setError("Password is required");
            return;
        }

        if (pass.length() < 6) {
            register_password.setError("At least six characters are required");
            return;
        }
        final Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("ID",ID);
        user.put("email", email);
        // register user
        fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    TeacherRef.collection("users")
                            .document(email)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("abc",e.getMessage());
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Error" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}