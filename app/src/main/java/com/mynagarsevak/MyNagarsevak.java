package com.mynagarsevak;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

/**
 * Created by sd on 24-04-2017.
 */

public class MyNagarsevak extends Application {

  FirebaseAuth mAuth;
  DatabaseReference mDataRef;

  @Override
  public void onCreate() {
    super.onCreate();

    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    mAuth = FirebaseAuth.getInstance();
    mDataRef= FirebaseDatabase.getInstance().getReference().child("users");


    OneSignal.startInit(this)
      .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
        @Override
        public void notificationReceived(OSNotification osNotification) {

        }
      })
      .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
        @Override
        public void notificationOpened(OSNotificationOpenResult osNotificationOpenResult) {

        }
      })
      .init();

    if(mAuth.getCurrentUser() != null){
      OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
        @Override
        public void idsAvailable(String userId, String registrationId) {
          Log.d("debug", "User:" + userId);
          mDataRef.child(mAuth.getCurrentUser().getUid()+"/oid").setValue(userId);
          if (registrationId != null)
            Log.d("debug", "registrationId:" + registrationId);
        }
      });
    }


  }
}
