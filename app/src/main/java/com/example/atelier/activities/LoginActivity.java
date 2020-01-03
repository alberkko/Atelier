package com.example.atelier.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.atelier.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;
    Button backBtn;
    TextView signupTextView;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        loginBtn = findViewById(R.id.login_button);
        signupTextView = findViewById(R.id.signupTextView);
        backBtn = findViewById(R.id.back_button);

        mAuthStateListenter = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "You just got logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    //Toast.makeText(LoginActivity.this,"Some Login Error",Toast.LENGTH_SHORT).show();
                }
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pasw = password.getText().toString();
                if (mail.isEmpty()) {
                    email.setError("Email pls");
                    email.requestFocus();
                } else if (pasw.isEmpty()) {
                    password.setError("Password pls");
                    password.requestFocus();
                } else if (mail.isEmpty() && pasw.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(mail.isEmpty() && pasw.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(mail, pasw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Error, Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intMain = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intMain);
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intSignup);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intBack = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intBack);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListenter);
    }
}

