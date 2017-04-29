package com.mynagarsevak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDetails extends AppCompatActivity {

  public static int APP_REQUEST_CODE = 99;
  public static final String USER_PREFS = "UserPreferences";

  private MaterialEditText mContactNo;
  private MaterialEditText mEmail;
  private MaterialEditText mName;
  private Button mSubmit;

  private MaterialBetterSpinner societySpinner, corporationSpinner, areaSpinner, stateSpinner, countrySpinner, divisionSpinner;

  DatabaseReference mDataRef, mPlacesRef;
  FirebaseAuth firebaseAuth;
  DataSnapshot Countries, States, Corporations, Divisions, Areas, Societies;
  ArrayList<String> countriesStrings, statesStrings, corporationsStrings, divisionsStrings, areaStrings, societiesStrings;
  ArrayAdapter<String> countriesAdapter, statesAdapter, corporationsAdapter, divisionsAdapter, areaAdapter, societiesAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_details);
//    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//    setSupportActionBar(toolbar);

    countriesStrings = new ArrayList<String>();
    statesStrings = new ArrayList<String>();
    corporationsStrings = new ArrayList<String>();
    divisionsStrings = new ArrayList<String>();
    areaStrings = new ArrayList<String>();
    societiesStrings = new ArrayList<String>();

    mDataRef = FirebaseDatabase.getInstance().getReference();
    firebaseAuth = FirebaseAuth.getInstance();
    mPlacesRef = mDataRef.child("places");
    mPlacesRef.keepSynced(true);

    mContactNo = (MaterialEditText) findViewById(R.id.user_post_contact);
    mEmail = (MaterialEditText) findViewById(R.id.user_post_email);
    mName = (MaterialEditText) findViewById(R.id.user_post_name);
    mSubmit = (Button) findViewById(R.id.user_post_submit);

    mEmail.setText(firebaseAuth.getCurrentUser().getEmail());
    mEmail.setEnabled(false);

    countrySpinner = (MaterialBetterSpinner) findViewById(R.id.user_post_country);
    stateSpinner = (MaterialBetterSpinner) findViewById(R.id.user_post_state);
    corporationSpinner = (MaterialBetterSpinner) findViewById(R.id.user_post_corporation);
    divisionSpinner = (MaterialBetterSpinner) findViewById(R.id.user_post_division);
    areaSpinner = (MaterialBetterSpinner) findViewById(R.id.user_post_area);
    societySpinner = (MaterialBetterSpinner) findViewById(R.id.user_post_society);

    countriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countriesStrings);
    statesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, statesStrings);
    corporationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, corporationsStrings);
    divisionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, divisionsStrings);
    areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, areaStrings);
    societiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, societiesStrings);

    countrySpinner.setAdapter(countriesAdapter);
    stateSpinner.setAdapter(statesAdapter);
    corporationSpinner.setAdapter(corporationsAdapter);
    divisionSpinner.setAdapter(divisionsAdapter);
    areaSpinner.setAdapter(areaAdapter);
    societySpinner.setAdapter(societiesAdapter);

    setSpinnerListeners();

  }

  private void setSpinnerListeners() {

    countrySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setDefaults(countrySpinner.getText().toString());
      }
    });

    stateSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setDefaults(countrySpinner.getText().toString(), stateSpinner.getText().toString());
      }
    });

    corporationSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setDefaults(countrySpinner.getText().toString(), stateSpinner.getText().toString(), corporationSpinner.getText().toString());
      }
    });

    divisionSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setDefaults(countrySpinner.getText().toString(), stateSpinner.getText().toString(), corporationSpinner.getText().toString(), divisionSpinner.getText().toString());
      }
    });

    areaSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setDefaults(countrySpinner.getText().toString(), stateSpinner.getText().toString(), corporationSpinner.getText().toString(), divisionSpinner.getText().toString(), areaSpinner.getText().toString());
      }
    });

    mPlacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        countriesStrings.clear();
        Countries = dataSnapshot;
        setDefaults();
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    mSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        submitDetails();
      }
    });
  }

  private void submitDetails() {
    if (TextUtils.isEmpty(countrySpinner.getText().toString()) || countrySpinner.getText() == null) {
      makeToast("Please select Country !");
      return;
    }

    if (TextUtils.isEmpty(stateSpinner.getText().toString()) || stateSpinner.getText() == null) {
      makeToast("Please select State !");
      return;
    }

    if (TextUtils.isEmpty(corporationSpinner.getText().toString()) || corporationSpinner.getText() == null) {
      makeToast("Please select Corporation !");
      return;
    }

    if (TextUtils.isEmpty(divisionSpinner.getText().toString()) || divisionSpinner.getText() == null) {
      makeToast("Please select Division !");
      return;
    }

    if (TextUtils.isEmpty(areaSpinner.getText().toString()) || areaSpinner.getText() == null) {
      makeToast("Please select Area !");
      return;
    }

    if (TextUtils.isEmpty(societySpinner.getText().toString()) || societySpinner.getText() == null) {
      makeToast("Please select Society !");
      return;
    }

    if (TextUtils.isEmpty(mName.getText().toString()) || mName.getText() == null) {
      makeToast("Full name required !");
      return;
    }

    if (TextUtils.isEmpty(mEmail.getText().toString()) || mEmail.getText() == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
      makeToast("Invalid Email Id!");
      return;
    }

    if (mContactNo.isCharactersCountValid()) {
      verify(mContactNo.getText());
    } else {
      Toast.makeText(UserDetails.this, "Please enter a valid contact.", Toast.LENGTH_LONG).show();
    }
  }

  private void verify(Editable text) {
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
      new AccountKitConfiguration.AccountKitConfigurationBuilder(
        LoginType.PHONE,
        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
    // ... perform additional configuration ...
    configurationBuilder.setInitialPhoneNumber(new PhoneNumber("+91", text.toString()));
    intent.putExtra(
      AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
      configurationBuilder.build());
    startActivityForResult(intent, APP_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(
    final int requestCode,
    final int resultCode,
    final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
      AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
      String toastMessage;
      if (loginResult.getError() != null) {
        toastMessage = loginResult.getError().getErrorType().getMessage();
      } else if (loginResult.wasCancelled()) {
        toastMessage = "Login Cancelled";
      } else {
        if (loginResult.getAccessToken() != null) {
          toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
        } else {
          toastMessage = String.format(
            "Success:%s...",
            loginResult.getAuthorizationCode().substring(0, 10));
        }


        HashMap<String, String> userProfile = new HashMap<String, String>();

        userProfile.put("name", mName.getText().toString().trim());
        userProfile.put("contactNumber", mContactNo.getText().toString().trim());
        userProfile.put("area", areaSpinner.getText().toString());
        userProfile.put("society", societySpinner.getText().toString());
        userProfile.put("division", divisionSpinner.getText().toString());
        userProfile.put("corporation", corporationSpinner.getText().toString());
        userProfile.put("state", stateSpinner.getText().toString());
        userProfile.put("country", countrySpinner.getText().toString());
        userProfile.put("email", mEmail.getText().toString().trim());
        userProfile.put("mobileIsVerified", "true");

        mDataRef.child("users/" + firebaseAuth.getCurrentUser().getUid().toString().trim() + "/profile").setValue(userProfile);

        SharedPreferences.Editor editor = getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit();
        editor.putString("name", userProfile.get("name"));
        editor.putString("contactNumber", userProfile.get("contactNumber"));
        editor.putString("area", userProfile.get("area"));
        editor.putString("society", userProfile.get("society"));
        editor.putString("division", userProfile.get("division"));
        editor.putString("corporation", userProfile.get("corporation"));
        editor.putString("state", userProfile.get("state"));
        editor.putString("country", userProfile.get("country"));
        editor.putString("email", userProfile.get("email"));
        editor.putString("mobileIsVerified", userProfile.get("mobileIsVerified"));
        editor.apply();

        AccountKit.logOut();
        startActivity(new Intent(UserDetails.this, HomeActivity.class));
        finish();
      }

      // Surface the result to your user in an appropriate way.
      Toast.makeText(
        this,
        toastMessage,
        Toast.LENGTH_LONG)
        .show();
    }
  }

  public void setDefaults(String country, String st, String cor, String div, String ar) {

    clearStrings();

    for (DataSnapshot c : Countries.getChildren()) {
      countriesStrings.add(c.getKey().toString());

      for (DataSnapshot states : c.getChildren()) {
        Log.i("States", states.getKey());
        if ((states.getRef().getParent().getKey().toString()).equals(country)) {
          statesStrings.add(states.getKey());
        }

        for (DataSnapshot corporation : states.getChildren()) {
          Log.i("Corporations", corporation.getKey());
          if ((corporation.getRef().getParent().getKey().toString()).equals(st)) {
            corporationsStrings.add(corporation.getKey());
          }

          for (DataSnapshot division : corporation.getChildren()) {
            Log.i("Divisions", division.getKey());
            if ((division.getRef().getParent().getKey().toString()).equals(cor)) {
              divisionsStrings.add(division.getKey());
            }

            for (DataSnapshot area : division.getChildren()) {
              Log.i("Areas", area.getKey());
              if ((area.getRef().getParent().getKey().toString()).equals(div)) {
                areaStrings.add(area.getKey());
              }

              for (DataSnapshot societies : area.getChildren()) {
                Log.i("Society", societies.getKey());
                if ((societies.getRef().getParent().getKey().toString()).equals(ar)) {
                  societiesStrings.add(societies.getKey());
                }
              }
            }
          }
        }
      }
    }

    notifyAdapters();

    countrySpinner.setText(country);
    stateSpinner.setText(st);
    corporationSpinner.setText(cor);
    divisionSpinner.setText(div);
    areaSpinner.setText(ar);
    societySpinner.setText(societiesAdapter.getItem(0));

  }


  public void setDefaults(String country, String st, String cor, String div) {

    clearStrings();

    for (DataSnapshot c : Countries.getChildren()) {
      countriesStrings.add(c.getKey().toString());

      for (DataSnapshot states : c.getChildren()) {
        Log.i("States", states.getKey());
        if ((states.getRef().getParent().getKey().toString()).equals(country)) {
          statesStrings.add(states.getKey());
        }

        for (DataSnapshot corporation : states.getChildren()) {
          Log.i("Corporations", corporation.getKey());
          if ((corporation.getRef().getParent().getKey().toString()).equals(st)) {
            corporationsStrings.add(corporation.getKey());
          }

          for (DataSnapshot division : corporation.getChildren()) {
            Log.i("Divisions", division.getKey());
            if ((division.getRef().getParent().getKey().toString()).equals(cor)) {
              divisionsStrings.add(division.getKey());
            }

            for (DataSnapshot area : division.getChildren()) {
              Log.i("Areas", area.getKey());
              if ((area.getRef().getParent().getKey().toString()).equals(div)) {
                areaStrings.add(area.getKey());
              }

              for (DataSnapshot societies : area.getChildren()) {
                Log.i("Society", societies.getKey());
                if ((societies.getRef().getParent().getKey().toString()).equals(areaStrings.get(0))) {
                  societiesStrings.add(societies.getKey());
                }
              }
            }
          }
        }
      }
    }

    notifyAdapters();

    countrySpinner.setText(country);
    stateSpinner.setText(st);
    corporationSpinner.setText(cor);
    divisionSpinner.setText(div);
    areaSpinner.setText(areaAdapter.getItem(0));
    societySpinner.setText(societiesAdapter.getItem(0));

  }

  public void setDefaults(String country, String st, String cor) {

    clearStrings();

    for (DataSnapshot c : Countries.getChildren()) {
      countriesStrings.add(c.getKey().toString());

      for (DataSnapshot states : c.getChildren()) {
        Log.i("States", states.getKey());
        if ((states.getRef().getParent().getKey().toString()).equals(country)) {
          statesStrings.add(states.getKey());
        }

        for (DataSnapshot corporation : states.getChildren()) {
          Log.i("Corporations", corporation.getKey());
          if ((corporation.getRef().getParent().getKey().toString()).equals(st)) {
            corporationsStrings.add(corporation.getKey());
          }

          for (DataSnapshot division : corporation.getChildren()) {
            Log.i("Divisions", division.getKey());
            if ((division.getRef().getParent().getKey().toString()).equals(cor)) {
              divisionsStrings.add(division.getKey());
            }

            for (DataSnapshot area : division.getChildren()) {
              Log.i("Areas", area.getKey());
              if ((area.getRef().getParent().getKey().toString()).equals(divisionsStrings.get(0))) {
                areaStrings.add(area.getKey());
              }

              for (DataSnapshot societies : area.getChildren()) {
                Log.i("Society", societies.getKey());
                if ((societies.getRef().getParent().getKey().toString()).equals(areaStrings.get(0))) {
                  societiesStrings.add(societies.getKey());
                }
              }
            }
          }
        }
      }
    }

    notifyAdapters();

    countrySpinner.setText(country);
    stateSpinner.setText(st);
    corporationSpinner.setText(cor);
    divisionSpinner.setText(divisionsAdapter.getItem(0));
    areaSpinner.setText(areaAdapter.getItem(0));
    societySpinner.setText(societiesAdapter.getItem(0));

  }

  public void setDefaults(String country, String st) {

    clearStrings();

    for (DataSnapshot c : Countries.getChildren()) {
      countriesStrings.add(c.getKey().toString());

      for (DataSnapshot states : c.getChildren()) {
        Log.i("States", states.getKey());
        if ((states.getRef().getParent().getKey().toString()).equals(country)) {
          statesStrings.add(states.getKey());
        }

        for (DataSnapshot corporation : states.getChildren()) {
          Log.i("Corporations", corporation.getKey());
          if ((corporation.getRef().getParent().getKey().toString()).equals(st)) {
            corporationsStrings.add(corporation.getKey());
          }

          for (DataSnapshot division : corporation.getChildren()) {
            Log.i("Divisions", division.getKey());
            if ((division.getRef().getParent().getKey().toString()).equals(corporationsStrings.get(0))) {
              divisionsStrings.add(division.getKey());
            }

            for (DataSnapshot area : division.getChildren()) {
              Log.i("Areas", area.getKey());
              if ((area.getRef().getParent().getKey().toString()).equals(divisionsStrings.get(0))) {
                areaStrings.add(area.getKey());
              }

              for (DataSnapshot societies : area.getChildren()) {
                Log.i("Society", societies.getKey());
                if ((societies.getRef().getParent().getKey().toString()).equals(areaStrings.get(0))) {
                  societiesStrings.add(societies.getKey());
                }
              }
            }
          }
        }
      }
    }

    notifyAdapters();

    countrySpinner.setText(country);
    stateSpinner.setText(st);
    corporationSpinner.setText(corporationsAdapter.getItem(0));
    divisionSpinner.setText(divisionsAdapter.getItem(0));
    areaSpinner.setText(areaAdapter.getItem(0));
    societySpinner.setText(societiesAdapter.getItem(0));

  }

  public void setDefaults(String country) {

    clearStrings();

    for (DataSnapshot c : Countries.getChildren()) {
      countriesStrings.add(c.getKey().toString());

      for (DataSnapshot states : c.getChildren()) {
        Log.i("States", states.getKey());
        if ((states.getRef().getParent().getKey().toString()).equals(country)) {
          statesStrings.add(states.getKey());
        }

        for (DataSnapshot corporation : states.getChildren()) {
          Log.i("Corporations", corporation.getKey());
          if ((corporation.getRef().getParent().getKey().toString()).equals(statesStrings.get(0))) {
            corporationsStrings.add(corporation.getKey());
          }

          for (DataSnapshot division : corporation.getChildren()) {
            Log.i("Divisions", division.getKey());
            if ((division.getRef().getParent().getKey().toString()).equals(corporationsStrings.get(0))) {
              divisionsStrings.add(division.getKey());
            }

            for (DataSnapshot area : division.getChildren()) {
              Log.i("Areas", area.getKey());
              if ((area.getRef().getParent().getKey().toString()).equals(divisionsStrings.get(0))) {
                areaStrings.add(area.getKey());
              }

              for (DataSnapshot societies : area.getChildren()) {
                Log.i("Society", societies.getKey());
                if ((societies.getRef().getParent().getKey().toString()).equals(areaStrings.get(0))) {
                  societiesStrings.add(societies.getKey());
                }
              }
            }
          }
        }
      }
    }

    notifyAdapters();

    countrySpinner.setText(country);
    stateSpinner.setText(statesAdapter.getItem(0));
    corporationSpinner.setText(corporationsAdapter.getItem(0));
    divisionSpinner.setText(divisionsAdapter.getItem(0));
    areaSpinner.setText(areaAdapter.getItem(0));
    societySpinner.setText(societiesAdapter.getItem(0));

  }

  public void setDefaults() {

    clearStrings();

    for (DataSnapshot c : Countries.getChildren()) {
      countriesStrings.add(c.getKey().toString());

      for (DataSnapshot states : c.getChildren()) {
        Log.i("States", states.getKey());
        if ((states.getRef().getParent().getKey().toString()).equals(countriesStrings.get(0))) {
          statesStrings.add(states.getKey());
        }

        for (DataSnapshot corporation : states.getChildren()) {
          Log.i("Corporations", corporation.getKey());
          if ((corporation.getRef().getParent().getKey().toString()).equals(statesStrings.get(0))) {
            corporationsStrings.add(corporation.getKey());
          }

          for (DataSnapshot division : corporation.getChildren()) {
            Log.i("Divisions", division.getKey());
            if ((division.getRef().getParent().getKey().toString()).equals(corporationsStrings.get(0))) {
              divisionsStrings.add(division.getKey());
            }

            for (DataSnapshot area : division.getChildren()) {
              Log.i("Areas", area.getKey());
              if ((area.getRef().getParent().getKey().toString()).equals(divisionsStrings.get(0))) {
                areaStrings.add(area.getKey());
              }

              for (DataSnapshot societies : area.getChildren()) {
                Log.i("Society", societies.getKey());
                if ((societies.getRef().getParent().getKey().toString()).equals(areaStrings.get(0))) {
                  societiesStrings.add(societies.getKey());
                }
              }
            }
          }
        }
      }
    }

    notifyAdapters();

    countrySpinner.setText(countriesAdapter.getItem(0));
    stateSpinner.setText(statesAdapter.getItem(0));
    corporationSpinner.setText(corporationsAdapter.getItem(0));
    divisionSpinner.setText(divisionsAdapter.getItem(0));
    areaSpinner.setText(areaAdapter.getItem(0));
    societySpinner.setText(societiesAdapter.getItem(0));

  }

  private void notifyAdapters() {
    countriesAdapter.notifyDataSetChanged();
    statesAdapter.notifyDataSetChanged();
    corporationsAdapter.notifyDataSetChanged();
    divisionsAdapter.notifyDataSetChanged();
    areaAdapter.notifyDataSetChanged();
    societiesAdapter.notifyDataSetChanged();
  }

  private void clearStrings() {
    countriesStrings.clear();
    statesStrings.clear();
    corporationsStrings.clear();
    divisionsStrings.clear();
    areaStrings.clear();
    societiesStrings.clear();
  }


  public void makeToast(String message) {
    Toast.makeText(UserDetails.this, message, Toast.LENGTH_LONG).show();
  }

}
