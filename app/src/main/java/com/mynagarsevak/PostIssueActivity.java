package com.mynagarsevak;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PostIssueActivity extends AppCompatActivity implements IPickResult {

  private ImageButton mIssueImage;

  private EditText mIssueTitle;
  private EditText mIssueDesc;

  private MaterialBetterSpinner typeSpinner;

  public Uri imageURI;
  public Uri downloadUrl;
  public static final String USER_PREFS = "UserPreferences";

  ProgressDialog progressDialog;

  private FirebaseStorage storage;
  private StorageReference storageRef;
  DatabaseReference mDataRef,mPlacesRef;
  FirebaseAuth firebaseAuth;

  public ArrayList a;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_issue);


    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Post Issue");
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    findViews();
    configViews();

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        postIssue();
      }
    });
  }

  private void configViews() {
    String[] TYPES = new String[]{
      "Private", "Public"
    };

    final ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(this,
      android.R.layout.simple_dropdown_item_1line, TYPES);
    typeSpinner.setAdapter(typesAdapter);

    mIssueImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        PickImageDialog.build(new PickSetup()
          .setSystemDialog(true)
        ).show(PostIssueActivity.this);
      }
    });
  }

  private void findViews() {
    mIssueImage = (ImageButton) findViewById(R.id.issue_image);
    mIssueTitle = (EditText) findViewById(R.id.issue_title);
    mIssueDesc = (EditText) findViewById(R.id.issue_description);

    mDataRef= FirebaseDatabase.getInstance().getReference();
    mPlacesRef = mDataRef.child("places");
    mPlacesRef.keepSynced(true);
    storage = FirebaseStorage.getInstance();
    firebaseAuth=FirebaseAuth.getInstance();

    typeSpinner = (MaterialBetterSpinner) findViewById(R.id.issue_type);

  }

  public void postIssue() {


    if(TextUtils.isEmpty(typeSpinner.getText().toString()) || typeSpinner.getText() == null){
      makeToast("Please select Type !");
      return;
    }

    if(TextUtils.isEmpty(mIssueTitle.getText().toString()) || mIssueTitle.getText() == null){
      makeToast("Title required !");
      return;
    }

    if(TextUtils.isEmpty(mIssueDesc.getText().toString()) || mIssueDesc.getText() == null){
      makeToast("Description required !");
      return;
    }

    progressDialog = ProgressDialog.show(this, "Posting Issue",
      "Please wait while we submit your issue", true);
    if(imageURI != null){

      mIssueImage.setDrawingCacheEnabled(true);
      mIssueImage.buildDrawingCache();
      Bitmap bitmap = mIssueImage.getDrawingCache();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      Toast.makeText(this,"Mime: "+getMimeType(getApplicationContext(),imageURI),Toast.LENGTH_LONG).show();

      if(getMimeType(getApplicationContext(),imageURI).equals("jpg") || getMimeType(getApplicationContext(),imageURI).equals("jpeg")){
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      }else if(getMimeType(getApplicationContext(),imageURI).equals("png")){
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      }
      byte[] data = baos.toByteArray();


      storageRef = storage.getReference().child("images/"+imageURI.getLastPathSegment());
      UploadTask uploadTask = storageRef.putBytes(data);

      // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
              }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                postnow();
              }
            });
    }else{
      postnow();
    }


  }

  public void postnow() {
    HashMap<String,String> newIssue=new HashMap<String,String>();
    String uid = firebaseAuth.getCurrentUser().getUid().trim();

    String key = mDataRef.child("issues").push().getKey();

    SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

    newIssue.put("userId",uid);
    newIssue.put("title",mIssueTitle.getText().toString());
    newIssue.put("description",mIssueDesc.getText().toString());
    newIssue.put("type",typeSpinner.getText().toString());
    newIssue.put("status","unresolved");
    newIssue.put("area", prefs.getString("area",null));
    newIssue.put("society", prefs.getString("society",null));
    newIssue.put("division", prefs.getString("division",null));
    newIssue.put("corporation", prefs.getString("corporation",null));
    newIssue.put("state", prefs.getString("state",null));
    newIssue.put("country", prefs.getString("country",null));
    newIssue.put("email", prefs.getString("email",null));

    if(imageURI != null && downloadUrl != null){
      newIssue.put("image",downloadUrl.toString());
    }

    mDataRef.child("issues/"+key+"/").setValue(newIssue);
    mDataRef.child("users/"+uid+"/issues/"+key+"/").setValue(newIssue);
    if(progressDialog != null){
      progressDialog.dismiss();
      makeToast("Your Issue has been posted!");
      finish();
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override
  public void onPickResult(PickResult r) {
    if (r.getError() == null) {
      imageURI = r.getUri();
      mIssueImage.setImageURI(imageURI);
      mIssueImage.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    } else {
      Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  public static String getMimeType(Context context, Uri uri) {
    String extension;
    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      final MimeTypeMap mime = MimeTypeMap.getSingleton();
      extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
    } else {
      extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
    }
    return extension;
  }

  public void makeToast(String message){
    Toast.makeText(PostIssueActivity.this,message,Toast.LENGTH_LONG).show();
  }
}
