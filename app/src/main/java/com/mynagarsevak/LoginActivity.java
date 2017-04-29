package com.mynagarsevak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = "LoginActivity";
  private SignInButton mGoogleBtn;
  private static final int RC_SIGN_IN = 1;
  private GoogleApiClient mGoogleApiClient;
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  public static final String USER_PREFS = "UserPreferences";

  private DatabaseReference mDataRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    initUI();
  }

  private void initUI() {
    mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);

    mDataRef = FirebaseDatabase.getInstance().getReference();

    mAuth = FirebaseAuth.getInstance();

    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {

          mDataRef.child("users/"+firebaseAuth.getCurrentUser().getUid().toString().trim()+"/profile")
            .addListenerForSingleValueEvent(new ValueEventListener() {

              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {

                mDataRef.keepSynced(true);
                if(dataSnapshot.getValue()==null){

                  startActivity(new Intent(LoginActivity.this, UserDetails.class));
                  finish();
                }
                else{
                  if(dataSnapshot.child("contactNumber").getValue().toString().trim()==null){
                    startActivity(new Intent(LoginActivity.this, UserDetails.class));
                    finish();
                  }
                  else{
                    SharedPreferences.Editor editor = getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit();
                    editor.putString("name", dataSnapshot.child("name").getValue().toString());
                    editor.putString("contactNumber", dataSnapshot.child("contactNumber").getValue().toString());
                    editor.putString("area", dataSnapshot.child("area").getValue().toString());
                    editor.putString("society", dataSnapshot.child("society").getValue().toString());
                    editor.putString("division", dataSnapshot.child("division").getValue().toString());
                    editor.putString("corporation", dataSnapshot.child("corporation").getValue().toString());
                    editor.putString("state", dataSnapshot.child("state").getValue().toString());
                    editor.putString("country", dataSnapshot.child("country").getValue().toString());
                    editor.putString("email", dataSnapshot.child("email").getValue().toString());
                    editor.putString("mobileIsVerified", dataSnapshot.child("mobileIsVerified").getValue().toString());
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                  }

                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
            });
        }
      }
    };
    // Configure Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
      .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
          Toast.makeText(LoginActivity.this, "You got an error !", Toast.LENGTH_SHORT).show();
        }
      })
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .build();
    mGoogleBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signIn();
      }
    });
  }

  private void signIn() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // Google Sign In was successful, authenticate with Firebase

        Toast.makeText(LoginActivity.this, "Successfull !", Toast.LENGTH_SHORT).show();
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseAuthWithGoogle(account);
      } else {
        // Google Sign In failed, update UI appropriately
        // ...
        Toast.makeText(LoginActivity.this, result.getStatus()+"", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

          // If sign in fails, display a message to the user. If sign in succeeds
          // the auth state listener will be notified and logic to handle the
          // signed in user can be handled in the listener.
          if (!task.isSuccessful()) {
            Log.w(TAG, "signInWithCredential", task.getException());
            Toast.makeText(getApplicationContext(), "Authentication failed.",
              Toast.LENGTH_SHORT).show();
          }
          // ...
        }
      });

  }


  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

}
