package com.example.atelier.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atelier.R;
import com.example.atelier.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText name, email, password;
    Button signupBtn;
    TextView loginTextView;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.nameText);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        signupBtn = findViewById(R.id.signup_button);
        loginTextView = findViewById(R.id.loginTextView);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = name.getText().toString().trim();
                final String mail = email.getText().toString().trim();
                String pasw = password.getText().toString().trim();
                if (mail.isEmpty()) {
                    email.setError("Seems like you forgot to enter your email");
                    email.requestFocus();
                } else if (pasw.isEmpty()) {
                    password.setError("You cannot login without a password. Duh!");
                    password.requestFocus();
                } else if (username.isEmpty()) {
                    name.setError("We will only be using your name just to know how to refer to you");
                    name.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    email.setError("Email address is invalid");
                    email.requestFocus();
                } else if (mail.isEmpty() && pasw.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Typically we would need some information to sign you up", Toast.LENGTH_SHORT).show();
                } else if (!(mail.isEmpty() && pasw.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(mail, pasw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                User user = new User(username, mail);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignupActivity.this, "Reg successful", Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(SignupActivity.this, "Didnt work", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    loginTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });
                }
            }
        });
    }
}