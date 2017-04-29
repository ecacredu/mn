package com.mynagarsevak.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mynagarsevak.PostIssueActivity;
import com.mynagarsevak.R;
import com.mynagarsevak.extras.ClickListener;
import com.mynagarsevak.extras.DividerItemDecoration;
import com.mynagarsevak.extras.RecyclerTouchListener;
import com.mynagarsevak.models.Issue;
import com.mynagarsevak.viewholders.IssueViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sd on 21-May-16.
 */
public class MyIssueFragment extends Fragment {

  public View mView;

  private List<Issue> myissuelist = new ArrayList<>();
  private RecyclerView recyclerView;

  private DatabaseReference mDatabase;
  FirebaseAuth firebaseAuth;

  AlertDialog diaBox;

  FirebaseRecyclerAdapter<Issue,IssueViewHolder> firebaseAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_myissue, container, false);

    mView = v;

    firebaseAuth=FirebaseAuth.getInstance();
    final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getActivity(),PostIssueActivity.class));
      }
    });

    return v;
  }

  @Override
  public void onStart() {
    super.onStart();

    recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);

    recyclerView.setHasFixedSize(false);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

    mDatabase = FirebaseDatabase.getInstance().getReference().child("users/"+  firebaseAuth.getCurrentUser().getUid().toString().trim()+"/issues");
    mDatabase.keepSynced(true);

     firebaseAdapter = new FirebaseRecyclerAdapter<Issue, IssueViewHolder>(
      Issue.class,
      R.layout.issue_list_row,
      IssueViewHolder.class,
      mDatabase
    ) {
      @Override
      protected void populateViewHolder(IssueViewHolder issueViewHolder, Issue issue, int i) {

        issueViewHolder.setTitle(issue.getTitle());
        issueViewHolder.setDesc(issue.getDescription());
        issueViewHolder.setImage(getContext(),issue.getImage());
      }
    };

    recyclerView.setAdapter(firebaseAdapter);

    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
      @Override
      public void onClick(View view, int position) {
        Issue i = firebaseAdapter.getItem(position);
        Toast.makeText(getContext(), i.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onLongClick(View view, int position) {
          confirmDelete(position);
      }
    }));
  }

  private void confirmDelete(int position) {
    diaBox = AskOption(position);
    if(!diaBox.isShowing()){
      diaBox.show();
    }
  }

  private AlertDialog AskOption(final int position)
  {
    final DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference().child("/issues");

    AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getContext())
      //set message, title, and icon
      .setTitle("Delete")
      .setMessage("Do you want to Delete")
      .setIcon(R.drawable.ic_delete)

      .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int whichButton) {
          //your deleting code
          final String issueKey = firebaseAdapter.getRef(position).getKey();

          firebaseAdapter.getRef(position).removeValue(
            new DatabaseReference.CompletionListener() {
              @Override
              public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                issueRef.child(issueKey).removeValue(new DatabaseReference.CompletionListener() {
                  @Override
                  public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    makeToast("Issue Deleted !");
                  }
                });
              }
            }
          );


          dialog.dismiss();
        }

      })

      .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      })
      .create();
    return myQuittingDialogBox;

  }

  public void makeToast(String message){
    Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
  }

  public static MyIssueFragment newInstance(String text) {

    MyIssueFragment f = new MyIssueFragment();
    Bundle b = new Bundle();
    b.putString("msg", text);

    f.setArguments(b);

    return f;
  }

}
