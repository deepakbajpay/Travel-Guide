package com.app.assist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, password;
    Button btnSignIn;
    TextView register, resetPassword;
    String strEmail, strPassword;
    FirebaseAuth mAuth;
    AVLoadingIndicatorView avl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Utilities.isLoggedIn(this)) {
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.login_email_et);
        password = (EditText) findViewById(R.id.login_password_et);
        btnSignIn = (Button) findViewById(R.id.login_sign_in_button);
        register = (TextView) findViewById(R.id.txt_register);
        resetPassword = (TextView) findViewById(R.id.txt_reset_password);
        avl = (AVLoadingIndicatorView) findViewById(R.id.avl_indicator_login);

        btnSignIn.setOnClickListener(this);
        register.setOnClickListener(this);
        resetPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_sign_in_button:
                strEmail = email.getText().toString().trim();
                strPassword = password.getText().toString().trim();

                if (!strEmail.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                    Utilities.showAlert(LoginActivity.this, "Invalid email", "Please enter a valid email address", "OK", "Cancel");
                } else if (strPassword.length() < 8) {
                    Utilities.showAlert(LoginActivity.this, "Invalid Password", "Password length can't be less than 8", "OK", "Cancel");
                } else
                    login();
                break;
            case R.id.txt_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_reset_password:
                String emailreset = email.getText().toString().trim();
                if (emailreset.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                    sendRecovetyMail(emailreset);
                } else {
                    Utilities.showAlert(LoginActivity.this, "Invalid email", "Please enter a valid email address to reset your password", "OK", "Cancel");
                }
        }
    }

    private void login() {
        btnSignIn.setVisibility(View.GONE);
        avl.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Utilities.setCurrentUser(LoginActivity.this, mAuth.getCurrentUser().getDisplayName(), strEmail);
                            Intent intent = new Intent(LoginActivity.this, MainPage.class);
                            startActivity(intent);
                            finish();
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        btnSignIn.setVisibility(View.VISIBLE);
                        avl.setVisibility(View.GONE);
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnSignIn.setVisibility(View.VISIBLE);
                        avl.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendRecovetyMail(final String email) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                btnSignIn.setVisibility(View.GONE);
                avl.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Recovery Email sent to " + email , Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(LoginActivity.this, "Failed to send Recovery EMail, Please try again", Toast.LENGTH_SHORT).show();
                                }
                                btnSignIn.setVisibility(View.VISIBLE);
                                avl.setVisibility(View.GONE);
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Failed to send Recovery Mail, Please try again", Toast.LENGTH_SHORT).show();
                        btnSignIn.setVisibility(View.VISIBLE);
                        avl.setVisibility(View.GONE);
                    }
                });
                return null;
            }
        }.execute();
    }
}
