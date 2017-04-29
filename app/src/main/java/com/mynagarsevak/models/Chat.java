package com.mynagarsevak.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sd on 25-04-2017.
 */

public class Chat  implements Parcelable {

  String userId,userName,agentId,agentName,userUnreadCount,agentUnreadCount,openDateTime,closeDateTime,status;

  public HashMap<String,Message> messages;

  public Chat() {
  }

  public Chat(String userId, String userName, String agentId, String agentName, String userUnreadCount, String agentUnreadCount, String openDateTime, String closeDateTime, String status, HashMap<String,Message> messages) {
    this.userId = userId;
    this.userName = userName;
    this.agentId = agentId;
    this.agentName = agentName;
    this.userUnreadCount = userUnreadCount;
    this.agentUnreadCount = agentUnreadCount;
    this.openDateTime = openDateTime;
    this.closeDateTime = closeDateTime;
    this.status = status;
    this.messages=messages;
  }

  public HashMap<String, Message> getMessages() {
    return messages;
  }

  public void setMessages(HashMap<String, Message> messages) {
    this.messages = messages;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public String getAgentName() {
    return agentName;
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public String getUserUnreadCount() {
    return userUnreadCount;
  }

  public void setUserUnreadCount(String userUnreadCount) {
    this.userUnreadCount = userUnreadCount;
  }

  public String getAgentUnreadCount() {
    return agentUnreadCount;
  }

  public void setAgentUnreadCount(String agentUnreadCount) {
    this.agentUnreadCount = agentUnreadCount;
  }

  public String getOpenDateTime() {
    return openDateTime;
  }

  public void setOpenDateTime(String openDateTime) {
    this.openDateTime = openDateTime;
  }

  public String getCloseDateTime() {
    return closeDateTime;
  }

  public void setCloseDateTime(String closeDateTime) {
    this.closeDateTime = closeDateTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  // Parcelling part
  public Chat(Parcel in){
    String[] data = new String[9];

    in.readStringArray(data);
    // the order needs to be the same as in writeToParcel() method
    this.userId = data[0];
    this.userName = data[1];
    this.agentId = data[2];
    this.agentName = data[3];
    this.userUnreadCount = data[4];
    this.agentUnreadCount = data[5];
    this.openDateTime = data[6];
    this.closeDateTime = data[7];
    this.status = data[8];
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeStringArray(new String[] {this.userId,
      this.userName,
      this.agentId,
      this.agentName,
      this.userUnreadCount,
      this.agentUnreadCount,
      this.openDateTime,
      this.closeDateTime,
      this.status});
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public Chat createFromParcel(Parcel in) {
      return new Chat(in);
    }

    public Chat[] newArray(int size) {
      return new Chat[size];
    }
  };
}
