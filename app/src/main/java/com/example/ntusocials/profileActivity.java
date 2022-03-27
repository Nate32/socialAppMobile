package com.example.ntusocials;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class profileActivity extends AppCompatActivity {

    private populateCards cards_val[];
    private arrayValues arrayValues;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;
    private DatabaseReference usrDb;

    ListView listView;
    List<populateCards> rowVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("NTUSocial");

        usrDb = FirebaseDatabase.getInstance().getReference().child("Registered Users");


        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        CheckUser();

        rowVals = new ArrayList<>();

        arrayValues = new arrayValues(this, R.layout.item, rowVals );
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayValues);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowVals.remove(0);
                arrayValues.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                populateCards object = (populateCards) dataObject;
                String usrId = object.getUsrId();
                usrDb.child(otherUsers).child("match").child("left").child(usrId).setValue(true);

                Toast.makeText(profileActivity.this, "Left!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                populateCards object = (populateCards) dataObject;
                String usrId = object.getUsrId();
                usrDb.child(otherUsers).child("match").child("right").child(usrId).setValue(true);
                swipeRightMatch(usrId);
                Toast.makeText(profileActivity.this, "Right!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(profileActivity.this, "Clicked!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void swipeRightMatch(String usrId) {
        DatabaseReference currentUserConnectDb =  usrDb.child(usrId).child("match").child("right").child(otherUsers);
        currentUserConnectDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !snapshot.child("match").child("right").hasChild(currentUId) ){
                    Toast.makeText(profileActivity.this, "matched", Toast.LENGTH_LONG);

                    String keyVal = FirebaseDatabase.getInstance().getReference().child("Messages").push().getKey();
                    usrDb.child(snapshot.getKey()).child("match").child("right").child(currentUId).child("messageId").setValue(keyVal);
                    usrDb.child(currentUId).child("match").child("right").child(snapshot.getKey()).child("messageId").setValue(keyVal);

                  //  usrDb.child(snapshot.getKey()).child("match").child("connection").child(currentUId).setValue(true);
                   // usrDb.child(currentUId).child("match").child("connection").child(snapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  String otherUsers;

    public void CheckUser(){
        DatabaseReference usrDb = FirebaseDatabase.getInstance().getReference().child("Registered Users");
        usrDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.getKey().equals(mAuth.getUid()) && !snapshot.child("match").child("right").hasChild(currentUId)
                        && !snapshot.child("match").child("left").hasChild(currentUId)){
                    String profileImage = "default";
                    if(!snapshot.child("profileImage").getValue().equals("default")) {
                        profileImage = snapshot.child("profileImage").getValue().toString();
                    }
                        populateCards val = new populateCards(mAuth.getUid(), snapshot.child("fullName").getValue().toString(), snapshot.child("profileImage").getValue().toString());
                        rowVals.add(val);
                        arrayValues.notifyDataSetChanged();
                        otherUsers = snapshot.getKey();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signOut(View view) {
        mAuth.signOut();
        Intent intent = new Intent(profileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void settingPage(View view) {
        Intent intent = new Intent(profileActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    public void matchWithUser(View view) {
        Intent intent = new Intent(profileActivity.this, UserMatchActivity.class);
        startActivity(intent);
        return;
    }
}