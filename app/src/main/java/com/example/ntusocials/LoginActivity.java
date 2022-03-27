package com.example.ntusocials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText LoginEmailInput, LoginPasswordInput;
    private ProgressBar progressBar;
    private FirebaseAuth profileAuth;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        LoginEmailInput = findViewById(R.id.Login_inputEmail);
        LoginPasswordInput = findViewById(R.id.Login_inputPassword);
        progressBar = findViewById(R.id.progress_bar_login);

        profileAuth = FirebaseAuth.getInstance();

        //user login button
        Button loginButton = findViewById(R.id.Login_user_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUserText = LoginEmailInput.getText().toString();
                String passwordUserText = LoginPasswordInput.getText().toString();

                if (TextUtils.isEmpty(emailUserText)) {
                    Toast.makeText(LoginActivity.this, "please enter your email", Toast.LENGTH_LONG).show();
                    LoginEmailInput.setError("email is required");
                    LoginEmailInput.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUserText).matches()) {
                    Toast.makeText(LoginActivity.this, "please enter valid email", Toast.LENGTH_LONG).show();
                    LoginEmailInput.setError("Valid email is required");
                    LoginEmailInput.requestFocus();
                }else if (TextUtils.isEmpty(passwordUserText)) {
                    Toast.makeText(LoginActivity.this, "please enter your password", Toast.LENGTH_LONG).show();
                    LoginPasswordInput.setError("password is required");
                    LoginPasswordInput.requestFocus();
                } else if (passwordUserText.length() < 5) {
                    Toast.makeText(LoginActivity.this, "password should be at least 8 characters", Toast.LENGTH_LONG).show();
                    LoginPasswordInput.setError("password needs to be longer");
                    LoginPasswordInput.requestFocus();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(emailUserText, passwordUserText);
                }

            }
        });


    }

    private void loginUser(String emailUser, String passwordUser) {

        profileAuth.signInWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // use the firbase to get current user
                    FirebaseUser firebaseUser = profileAuth.getCurrentUser();

                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "You have successfully logged in", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, profileActivity.class);
                        startActivity(intent);
                    }else {
                        firebaseUser.sendEmailVerification();
                        profileAuth.signOut();
                        viewAlertDialog();

                    }
                } else {
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        LoginEmailInput.setError("User does not exist, Try again");
                        LoginEmailInput.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        LoginEmailInput.setError("Invalid user details , Try again");
                        LoginEmailInput.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void viewAlertDialog() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email must be verified");
        builder.setMessage("please verify your email to login");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //while the user is logged in take user to profile

        if(profileAuth.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already Login", Toast.LENGTH_LONG).show();

            startActivity(new Intent(LoginActivity.this,  profileActivity.class));
            finish(); // close the login activity
        } else {
            Toast.makeText(LoginActivity.this, "You may login", Toast.LENGTH_LONG).show();
        }
    }
}