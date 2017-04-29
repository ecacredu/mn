package com.mynagarsevak;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mynagarsevak.extras.DividerItemDecoration;
import com.mynagarsevak.models.Chat;
import com.mynagarsevak.models.Issue;
import com.mynagarsevak.models.Message;
import com.mynagarsevak.viewholders.IssueViewHolder;
import com.mynagarsevak.viewholders.MessageViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

  private RecyclerView recyclerView;

  private DatabaseReference mDatabase;
  private DatabaseReference mUserRef;
  FirebaseAuth firebaseAuth;

  FirebaseRecyclerAdapter<Message,MessageViewHolder> firebaseAdapter;

  Chat currentChat;
  String chatKey;

  private EditText mChatInput;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    currentChat = (Chat) getIntent().getParcelableExtra("CHAT_INSTANCE");
    chatKey = getIntent().getStringExtra("CHAT_KEY");

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(currentChat.getAgentName());
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    mChatInput = (EditText) findViewById(R.id.chatinput);

    recyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);

    recyclerView.setHasFixedSize(false);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    mLayoutManager.setStackFromEnd(true);
//    mLayoutManager.setReverseLayout(true);
    recyclerView.setLayoutManager(mLayoutManager);


    firebaseAuth = FirebaseAuth.getInstance();
    mUserRef = FirebaseDatabase.getInstance().getReference().child("users/"+firebaseAuth.getCurrentUser().getUid().trim());
    mUserRef.keepSynced(true);
    mDatabase = FirebaseDatabase.getInstance().getReference().child("chats/"+chatKey+"/messages");
    mDatabase.keepSynced(true);


    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        recyclerView.scrollToPosition(firebaseAdapter.getItemCount()-1);
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });

    firebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
      Message.class,
      R.layout.item_chat_mine,
      MessageViewHolder.class,
      mDatabase
    ) {
      @Override
      protected void populateViewHolder(MessageViewHolder messageViewHolder, Message message, int i) {

//        issueViewHolder.setTitle(issue.getTitle());
        messageViewHolder.setLayout(message.getSenderType());
        messageViewHolder.setMessage(message.getMessage());
        messageViewHolder.setMsgTime(message.getDate());

      }
    };

    recyclerView.setAdapter(firebaseAdapter);


    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sendfab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if(TextUtils.isEmpty(mChatInput.getText())){
          return;
        }
        sendMessage(mChatInput.getText().toString());
      }
    });
  }

  private void sendMessage(String text) {


    HashMap<String,String> newMsg=new HashMap<String, String>();

    newMsg.put("message",text);
    newMsg.put("senderType","user");
    newMsg.put("date",new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
    newMsg.put("timestamp",new Date().getTime()+"");

    String msgKey = mDatabase.push().getKey();

    mDatabase.child(msgKey).setValue(newMsg);
    mChatInput.setText("");
//    mChatInput.clearFocus();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

}
