package com.example.ntusocials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserMatchActivity extends AppCompatActivity {

    private RecyclerView.Adapter matchArrAdapt;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_match);

        getSupportActionBar().setTitle("Matches");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = (RecyclerView) findViewById(R.id.listView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(UserMatchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        matchArrAdapt = new MatchAdaptArray(getMatchData(), UserMatchActivity.this);
        recyclerView.setAdapter(matchArrAdapt);

        valUserMatchId();


    }

    private void valUserMatchId() {
        DatabaseReference listMatch = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(currentUserId).child("match").child("right");
        listMatch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot match: snapshot.getChildren()){
                        getTheMatchInfo(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTheMatchInfo(String key) {
        DatabaseReference listMatchKey = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(key);

        listMatchKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String snapshotUserId = snapshot.getKey();
                    String fullName = "";
                    String imageUrl = "";

                    if(snapshot.child("fullName").getValue() != null){
                        fullName = snapshot.child("fullName").getValue().toString();
                        imageUrl = snapshot.child("profileImage").getValue().toString();
                    }


                    MatchPojo matchPojo = new MatchPojo(snapshotUserId, fullName, imageUrl);
                    matchesVal.add(matchPojo);
                    matchArrAdapt.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<MatchPojo> matchesVal = new ArrayList<>();
    private List<MatchPojo> getMatchData() {
        return matchesVal;
    }
}