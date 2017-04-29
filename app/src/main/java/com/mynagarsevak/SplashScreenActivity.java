package com.mynagarsevak;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

  private DatabaseReference mDataRef;
  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    mDataRef = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    if(mAuth.getCurrentUser() == null){

      new Handler().postDelayed(new Runnable(){
        @Override
        public void run() {
                /* Create an Intent that will start the Menu-Activity. */
          Intent mainIntent = new Intent(SplashScreenActivity.this,LoginActivity.class);
          SplashScreenActivity.this.startActivity(mainIntent);
          SplashScreenActivity.this.finish();
        }
      }, 1000);
    }else{
      mDataRef.child("users/"+mAuth.getCurrentUser().getUid().toString().trim()+"/profile")
        .addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getValue()==null){

              startActivity(new Intent(SplashScreenActivity.this, UserDetails.class));
              finish();
            }
            else{
              if(dataSnapshot.child("contactNumber").getValue().toString().trim()==null){
                startActivity(new Intent(SplashScreenActivity.this, UserDetails.class));
                finish();
              }
              else{
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
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
}
