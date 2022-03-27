package com.example.ntusocials;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMessageActivity extends AppCompatActivity {

    private MessageAdaptor messageArrAdapt;
    private RecyclerView.LayoutManager messageLayoutManager;
    private RecyclerView messageRecyclerView;
    private DatabaseReference databaseReference, messageReference;
    private String currentUserId;
    private String match_id;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String message_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);

        getSupportActionBar().setTitle("Messages");
        match_id = getIntent().getExtras().getString("match_id");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Registered Users").child(currentUserId).child("match").child("right").child(match_id).child("matchId");
        messageReference = FirebaseDatabase.getInstance().getReference().child("Messages");

        getMessageId();

        messageRecyclerView = (RecyclerView) findViewById(R.id.messageListView);
        messageRecyclerView.setNestedScrollingEnabled(false);
        messageRecyclerView.setHasFixedSize(false);


        messageLayoutManager = new LinearLayoutManager(UserMessageActivity.this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);
        messageArrAdapt = new MessageAdaptor(getMessageValue(), UserMessageActivity.this);
        messageRecyclerView.setAdapter(messageArrAdapt);

        messageEditText = findViewById(R.id.send_messsage_input);
        sendMessageButton = findViewById(R.id.send_button_message);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();

            }
        });

    }

    private void sendMessage(){
        String  message_text = messageEditText.getText().toString();
        if(!message_text.isEmpty()){
            DatabaseReference messageDatabase = messageReference.push();

            Map messages = new HashMap();
            messages.put("sentBy", currentUserId);
            messages.put("message", message_text);

            messageDatabase.setValue(messages);

        }

        messageEditText.setText(null);
    }

    private void getMessageId(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    message_id = snapshot.getValue().toString();
                    messageReference = messageReference.child(message_id);

                    getMessages(message_id);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessages(String Key) {
        DatabaseReference listMessages = FirebaseDatabase.getInstance().getReference().child("Messages").child(Key);
        listMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String message = "";
                    String sentBy = "";

                    message = snapshot.child("message").getValue().toString();
                    sentBy = snapshot.child("sentBy").getValue().toString();
                    if(message != null && sentBy != null) {
                        Boolean currentMessenger = false;

                        if(currentMessenger.equals(currentUserId)){
                            currentMessenger = true;
                        }
                        MessagePojo messagePojo = new MessagePojo(message, currentMessenger);
                        messageValue.add(messagePojo);
                        messageArrAdapt.notifyDataSetChanged();
                    }
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

    private ArrayList<MessagePojo> messageValue = new ArrayList<>();
    private List<MessagePojo> getMessageValue() {
        return messageValue;
    }
}