package com.app.assist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.wang.avi.AVLoadingIndicatorView;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password, confirmPassword;
    Button register;
    FirebaseAuth mAuth;
    AVLoadingIndicatorView avl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth =FirebaseAuth.getInstance();
        name = (EditText) findViewById(R.id.register_name_et);
        email = (EditText) findViewById(R.id.regisiter_email_et);
        password = (EditText) findViewById(R.id.register_password_et);
        confirmPassword = (EditText) findViewById(R.id.register_password_reenter_et);
        avl = (AVLoadingIndicatorView)findViewById(R.id.avl_indicator_register);
        register = (Button) findViewById(R.id.register_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namestr, emailstr, passwordstr, confirmPasswordstr;
                namestr = name.getText().toString().trim();
                emailstr = email.getText().toString().trim();
                passwordstr = password.getText().toString().trim();
                confirmPasswordstr = confirmPassword.getText().toString().trim();

                if (namestr.length() == 0) {
                    Utilities.showAlert(RegisterActivity.this, "Invalid Name", "Please enter your name", "OK", "Cancel");
                } else if (!emailstr.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                    Utilities.showAlert(RegisterActivity.this, "Invalid email", "Please enter a valid email address", "OK", "Cancel");
                } else if (passwordstr.length() < 8 || confirmPasswordstr.length() < 8) {
                    Utilities.showAlert(RegisterActivity.this, "Invalid Password", "Password length can't be less than 8", "OK", "Cancel");
                } else if (!passwordstr.equals(confirmPasswordstr)) {
                    Utilities.showAlert(RegisterActivity.this, "Password Mismatch", "Please enter matching passwords", "OK", "Cancel");

                } else {

                   registerUser();
                }
            }
        });


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void registerUser() {
        register.setVisibility(View.GONE);
        avl.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString().trim()).build();

                            user.updateProfile(profileUpdates);
                            Utilities.setCurrentUser(RegisterActivity.this,name.getText().toString().trim(),email.getText().toString().trim());

                            Intent intent = new Intent(RegisterActivity.this,MainPage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        register.setVisibility(View.VISIBLE);
                        avl.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        register.setVisibility(View.VISIBLE);
                        avl.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Registration faild. please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
