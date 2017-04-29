package com.mynagarsevak.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mynagarsevak.PostIssueActivity;
import com.mynagarsevak.R;
import com.mynagarsevak.extras.DividerItemDecoration;
import com.mynagarsevak.models.Issue;
import com.mynagarsevak.viewholders.IssueViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sd on 21-May-16.
 */
public class PublicIssueFragment extends Fragment {

  public View mView;

  private List<Issue> publicissuelist = new ArrayList<>();
  private RecyclerView recyclerView;

  private DatabaseReference mDatabase;
  FirebaseAuth firebaseAuth;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_publicissue, container, false);

    mView = v;

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

    recyclerView = (RecyclerView) mView.findViewById(R.id.public_recycler_view);
    recyclerView.setHasFixedSize(false);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

    mDatabase = FirebaseDatabase.getInstance().getReference().child("issues");
    Query query = mDatabase.orderByChild("type").equalTo("Public");
    mDatabase.keepSynced(true);

    FirebaseRecyclerAdapter<Issue,IssueViewHolder> firebaseAdapter = new FirebaseRecyclerAdapter<Issue, IssueViewHolder>(
      Issue.class,
      R.layout.issue_list_row,
      IssueViewHolder.class,
      query
    ) {
      @Override
      protected void populateViewHolder(IssueViewHolder issueViewHolder, Issue issue, int i) {

        issueViewHolder.setTitle(issue.getTitle());
        issueViewHolder.setDesc(issue.getDescription());
        issueViewHolder.setImage(getContext(),issue.getImage());
      }
    };

    recyclerView.setAdapter(firebaseAdapter);

  }

  public static PublicIssueFragment newInstance(String text) {

    PublicIssueFragment f = new PublicIssueFragment();
    Bundle b = new Bundle();
    b.putString("msg", text);

    f.setArguments(b);

    return f;
  }
}
