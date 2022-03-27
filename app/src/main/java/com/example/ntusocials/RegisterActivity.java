package com.example.ntusocials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerFullName, registerInputEmail, registerInputAge, registerInputPassword;

    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonGender;
    private FirebaseAuth mAuth;

    //private ImageView profile_image;

    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this, "you can register now", Toast.LENGTH_LONG).show();


        registerFullName = findViewById(R.id.register_inputName);
        registerInputEmail = findViewById(R.id.register_inputEmail);
        registerInputAge = findViewById(R.id.register_inputAge);
        registerInputPassword = findViewById(R.id.register_inputPassword);
        progressBar = findViewById(R.id.progress_bar);

        radioGroupRegisterGender = findViewById(R.id.register_gender_radio);
        radioGroupRegisterGender.clearCheck();
       // profile_image = findViewById(R.id.profile_image);

        Button registerButton = findViewById(R.id.register_user_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int genderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonGender = findViewById(genderId);

                //get the data

                String fullNameText = registerFullName.getText().toString();
                String emailText = registerInputEmail.getText().toString();
                String ageText = registerInputAge.getText().toString();
                String passwordText = registerInputPassword.getText().toString();
                String genderText;
                String profileImage = "default";


                if (TextUtils.isEmpty(fullNameText)) {
                    Toast.makeText(RegisterActivity.this, "please enter your full name", Toast.LENGTH_LONG).show();
                    registerFullName.setError("Valid name is required");
                    registerFullName.requestFocus();
                } else if (TextUtils.isEmpty(emailText)) {
                    Toast.makeText(RegisterActivity.this, "please enter your email", Toast.LENGTH_LONG).show();
                    registerInputEmail.setError("email is required");
                    registerInputEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    Toast.makeText(RegisterActivity.this, "please enter valid email", Toast.LENGTH_LONG).show();
                    registerInputEmail.setError("Valid email is required");
                    registerInputEmail.requestFocus();
                } else if (TextUtils.isEmpty(ageText)) {
                    Toast.makeText(RegisterActivity.this, "please enter your age", Toast.LENGTH_LONG).show();
                    registerInputAge.setError("age is required");
                    registerInputAge.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "please enter your gender", Toast.LENGTH_LONG).show();
                    radioButtonGender.setError("gender is required");
                    radioButtonGender.requestFocus();
                } else if (TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(RegisterActivity.this, "please enter your password", Toast.LENGTH_LONG).show();
                    registerInputPassword.setError("password is required");
                    registerInputPassword.requestFocus();
                } else if (passwordText.length() < 5) {
                    Toast.makeText(RegisterActivity.this, "password should be at least 8 characters", Toast.LENGTH_LONG).show();
                    registerInputPassword.setError("password needs to be longer");
                    registerInputPassword.requestFocus();
                } else {
                    genderText = radioButtonGender.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    userRegister(fullNameText, emailText, ageText, passwordText, genderText, profileImage);
                }

            }
        });


    }

    // register user

    private void userRegister(String fullNameText, String emailText, String ageText, String passwordText, String genderText, String profileImage) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "You have successfully registered", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // updating the name of the user

                    UserProfileChangeRequest changeRequestProfile = new  UserProfileChangeRequest.Builder().setDisplayName(fullNameText).build();
                    firebaseUser.updateProfile(changeRequestProfile);
                    // user data into the database firebase

                  ///  String profileImage = "default";
                    ReadAndWriteUser  writeUser  = new ReadAndWriteUser(ageText, genderText, fullNameText, profileImage);

                    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Registered Users");
                   // profileRef.updateChildren(userDetail);

                    profileRef.child(firebaseUser.getUid()).setValue(writeUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // verify email
                            if(task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User Registered successfully. Very email", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, profileActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                // this closes the register activity ;
                                finish();
                            } else{
                                Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        registerInputPassword.setError("your email is invalid or already in use");
                        registerInputPassword.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        registerInputPassword.setError("user already registered");
                        registerInputPassword.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG);

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}