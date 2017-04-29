package com.mynagarsevak.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mynagarsevak.ChatActivity;
import com.mynagarsevak.R;
import com.mynagarsevak.extras.ClickListener;
import com.mynagarsevak.extras.DividerItemDecoration;
import com.mynagarsevak.extras.RecyclerTouchListener;
import com.mynagarsevak.models.Chat;
import com.mynagarsevak.models.Issue;
import com.mynagarsevak.models.Message;
import com.mynagarsevak.viewholders.ChatViewHolder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sd on 21-May-16.
 */
public class MessagesFragment extends Fragment {

  private String newQueryText = "";

  private View mView;
  private List<Message> mymessagelist = new ArrayList<>();
  private RecyclerView recyclerView;

  private String profileName;

  HashMap<String,String> userData;
  HashMap<String,Object> userDataProfile;

  private DatabaseReference mDatabase;
  DatabaseReference mUserDataRef;
  DatabaseReference mChatDataRef;
  FirebaseAuth firebaseAuth;
  FirebaseRecyclerAdapter<Chat,ChatViewHolder> firebaseAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_message, container, false);

    firebaseAuth=FirebaseAuth.getInstance();

    FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.newmessagefab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        startActivity(new Intent(getContext(), ChatActivity.class));
        AskQuery();
      }
    });

    mView = v;

    return v;
  }

  @Override
  public void onStart() {
    super.onStart();

    recyclerView = (RecyclerView) mView.findViewById(R.id.message_recycler_view);

    recyclerView.setHasFixedSize(false);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


    mDatabase = FirebaseDatabase.getInstance().getReference().child("chats");
    Query query = mDatabase.orderByChild("userId").equalTo(firebaseAuth.getCurrentUser().getUid().trim());
    mDatabase.keepSynced(true);

    firebaseAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
      Chat.class,
      R.layout.message_list_row,
      ChatViewHolder.class,
      query
    ) {

      @Override
      protected void populateViewHolder(final ChatViewHolder chatViewHolder, final Chat chat, int i) {
        final String chatKey = getRef(i).getKey();
        mDatabase.child(chatKey+"/messages").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnap : dataSnapshot.getChildren()){
              chatViewHolder.setSender(chat.getAgentName());
              chatViewHolder.setMessage(childSnap.child("message").getValue().toString());
              chatViewHolder.setTime(childSnap.child("date").getValue().toString(),getContext());
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });


      }
    };

    recyclerView.setAdapter(firebaseAdapter);

    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
      @Override
      public void onClick(View view, int position) {
        Chat i = firebaseAdapter.getItem(position);
        String ckey = firebaseAdapter.getRef(position).getKey();
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("CHAT_INSTANCE", new Chat(i.getUserId(),i.getUserName(),i.getAgentId(),i.getAgentName(),i.getUserUnreadCount(),i.getAgentUnreadCount(),i.getOpenDateTime(),i.getCloseDateTime(),i.getStatus(),i.getMessages()));
        intent.putExtra("CHAT_KEY", ckey);
        startActivity(intent);
      }

      @Override
      public void onLongClick(View view, int position) {

      }
    }));

    mUserDataRef = FirebaseDatabase.getInstance().getReference().child("users/"+firebaseAuth.getCurrentUser().getUid().trim());
    mUserDataRef.keepSynced(true);
    mUserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        profileName = (String) dataSnapshot.child("profile/name").getValue();
        userData= new HashMap<String, String>();
        userDataProfile = new HashMap<String, Object>();
        userData.put("oid",((dataSnapshot.child("oid").getValue()==null)? "":dataSnapshot.child("oid").getValue().toString()));
        userDataProfile.put("profile",((dataSnapshot.child("profile").getValue()==null)? "":dataSnapshot.child("profile").getValue()));
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
    mChatDataRef = FirebaseDatabase.getInstance().getReference().child("chats/");
    mChatDataRef.keepSynced(true);

  }

  private void AskQuery() {

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("Query Topic");

    TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setMargins(16,8,8,16);
    final EditText input = new EditText(getContext());
    input.setLayoutParams(params);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        newQueryText = input.getText().toString();
        submitQuery(newQueryText);
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  private void submitQuery(final String newQueryText) {

    mUserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        Boolean shouldUpload=false;

        if(dataSnapshot.child("userPending").getValue() == null){
          shouldUpload=true;
        }else if(dataSnapshot.child("userPending").getValue().toString().equals("false")){
          shouldUpload=true;
        }

        if(shouldUpload){

          HashMap<String,String> newQuery=new HashMap<String, String>();

          final String key = mChatDataRef.push().getKey();

          newQuery.put("userId",firebaseAuth.getCurrentUser().getUid().trim());
          newQuery.put("userName",profileName);
          newQuery.put("oid",userData.get("oid"));
          newQuery.put("agentId","any");
          newQuery.put("agentName","any");
          newQuery.put("openDateTime",new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
          newQuery.put("openDateTimeStamp",new Date().getTime()+"");
          newQuery.put("status","unresolved");
//          newQuery.put("closeDateTime","");
          newQuery.put("userUnreadCount","0");
          newQuery.put("agentUnreadCount","1");

          mChatDataRef.child(key).setValue(newQuery, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
              makeToast("Query submitted!");
              mUserDataRef.child("userPending").setValue("true");

              HashMap<String,String> newMsg=new HashMap<String, String>();

              newMsg.put("message",newQueryText);
              newMsg.put("senderType","user");
              newMsg.put("date",new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
              newMsg.put("timestamp",new Date().getTime()+"");

              String msgKey = mChatDataRef.child(key+"/messages/").push().getKey();

              mChatDataRef.child(key+"/messages/"+msgKey).setValue(newMsg);
              mChatDataRef.child(key+"/user").setValue(userDataProfile.get("profile"));
            }
          });

        }else{
          makeToast("Your previous query is still pending, please wait!");
          return;
        }

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

  }

  public void makeToast(String message){
    Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
  }

  public static MessagesFragment newInstance(String text) {

    MessagesFragment f = new MessagesFragment();
    Bundle b = new Bundle();
    b.putString("msg", text);

    f.setArguments(b);

    return f;
  }
}
