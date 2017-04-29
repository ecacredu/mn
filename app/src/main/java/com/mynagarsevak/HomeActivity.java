package com.mynagarsevak;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.mynagarsevak.adapters.MyPagerAdapter;
import com.mynagarsevak.fragments.MyIssueFragment;

/**
 * Created by sd on 18-04-2017.
 */

public class HomeActivity extends AppCompatActivity {

  private static final String TAG = "HomeActivity";
  private ViewPager mViewPager;
  private GoogleApiClient mGoogleApiClient;
  private Toolbar mToolBar;
  private NavigationTabStrip mTopNavigationTabStrip;

  public MyPagerAdapter mAdapter;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home_activity);

    initUI();
    setUI();
  }

  private void initUI() {
    mToolBar = (Toolbar) findViewById(R.id.toolbar);
    mViewPager = (ViewPager) findViewById(R.id.vp);
    mTopNavigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts_top);
  }

  private void setUI() {
    mAuth = FirebaseAuth.getInstance();

    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
          startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
      }
    };
    setSupportActionBar(mToolBar);
    mToolBar.setTitle("My Nagarsevak");
    mAdapter = new MyPagerAdapter(getSupportFragmentManager());
    mViewPager.setAdapter(mAdapter);
    mTopNavigationTabStrip.setTitles("My Issues", "Public Issues", "Message");
    mTopNavigationTabStrip.setAnimationDuration(150);
    mTopNavigationTabStrip.setStripType(NavigationTabStrip.StripType.LINE);
    mTopNavigationTabStrip.setViewPager(mViewPager, 0);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
      .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
          Toast.makeText(HomeActivity.this, "You got an error !", Toast.LENGTH_SHORT).show();
        }
      })
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .build();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_logout) {
      mAuth.signOut();
      Auth.GoogleSignInApi.signOut(mGoogleApiClient);
      return true;
    }

    return super.onOptionsItemSelected(item);
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
