package com.example.ntusocials;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText PersonalityTypeInput, QuestionInput;

    private Button settingBack, settingConfirmChange;

    private ImageView profileImageInput;

    private FirebaseAuth mAuth;

    private DatabaseReference userDatabase;
    private  String userId, personalityType, question, profileImage;

    private Uri resultUri;

   // private final ActivityResultRegistry;
    private ActivityResultLauncher<Intent> activityLauncher;
    private Context context;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");

        PersonalityTypeInput = findViewById(R.id.personality_inputType);
        QuestionInput = findViewById(R.id.question_input);
        settingBack = findViewById(R.id.settings_back_btn);
        settingConfirmChange = findViewById(R.id.settings_confirm_btn);

        profileImageInput = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userId);

        selectUserDetails();

        activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()  == Activity.RESULT_OK && result.getData() != null){
                    Intent data = result.getData();
                    Uri imageUrl = data.getData();
                    resultUri = imageUrl;
                    profileImageInput.setImageURI(resultUri);
                }
            }
        });

        profileImageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityLauncher.launch(intent);

            }
        });
        settingConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettingChange();
            }
        });
        settingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });


    }

    private void selectUserDetails() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> val = (Map<String, Object>) snapshot.getValue();
                    if (val.get("personality_type") != null){
                        personalityType = val.get("personality_type").toString();
                        PersonalityTypeInput.setText(personalityType);
                    }
                    if (val.get("question") != null){
                        question = val.get("question").toString();
                        QuestionInput.setText(question);
                    }
                    if (val.get("profileImage") != null){
                        profileImage = val.get("profileImage").toString();
                        switch (profileImage){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(profileImageInput);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImage).into(profileImageInput);
                                break;
                        }
                       // Glide.with(getApplication()).load(profileImage).into(profileImageInput);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveSettingChange() {
        personalityType = PersonalityTypeInput.getText().toString();
        question = QuestionInput.getText().toString();

        Map settingUser = new HashMap();

        settingUser.put("personality_type", personalityType);
        settingUser.put("question", question);

        userDatabase.updateChildren(settingUser);

        if(resultUri != null){
            storageReference = FirebaseStorage.getInstance().getReference().child("profileImage").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream comp =new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, comp);
            byte[] val = comp.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(val);
            uploadTask.addOnFailureListener((e) -> {finish();});
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference data = taskSnapshot.getStorage();
                    Task<Uri> getImage = data.getDownloadUrl();
                    getImage.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                               Toast.makeText(SettingsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                              // Uri downloadUri = task.getResult();
                                String url = uri.toString();
                                Map userDetail = new HashMap();
                                userDetail.put("profileImage", url);
                                userDatabase.updateChildren(userDetail);

                        }
                    });

                    finish();
                    return;
                }
            });

        }else {
            finish();
        }

    }

}