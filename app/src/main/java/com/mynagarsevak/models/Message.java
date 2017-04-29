package com.mynagarsevak.models;

/**
 * Created by sd on 22-04-2017.
 */

public class Message {

  String message,senderType,timestamp,date;

  public Message() {
  }

  public Message(String message, String senderType, String date, String timestamp) {
    this.message = message;
    this.senderType = senderType;
    this.date = date;
    this.timestamp=timestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSenderType() {
    return senderType;
  }

  public void setSenderType(String senderType) {
    this.senderType = senderType;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
}
