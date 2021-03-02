package com.example.pantattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
private EditText forget_pass_email;
private Button forget_pass_link_btn;
private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        forget_pass_email = findViewById(R.id.forget_pass_email);
        forget_pass_link_btn = findViewById(R.id.forget_pass_link_btn);
        fAuth = FirebaseAuth.getInstance();
        forget_pass_link_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetPassword();
            }
        });
    }

    private void forgetPassword() {
        String email = forget_pass_email.getText().toString().trim();
        if (TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            forget_pass_email.setError("Email is required");
        }
        else {
            fAuth.sendPasswordResetEmail(email);
            Toast.makeText(ForgetPasswordActivity.this, "Password reset email has been sent to your registered email", Toast.LENGTH_LONG).show();
            //finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}