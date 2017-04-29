package com.mynagarsevak.models;

/**
 * Created by sd on 19-04-2017.
 */

public class Issue {

  private String title, area, society, description, type, status, uid, image;

  public Issue() {
  }

  public Issue(String title, String area, String society, String description, String type, String status, String uid, String image) {
    this.title = title;
    this.area = area;
    this.society = society;
    this.description = description;
    this.type = type;
    this.status = status;
    this.uid = uid;
    this.image=image;
  }

  public Issue(String title, String area, String society, String description, String type, String status, String uid) {
    this.title = title;
    this.area = area;
    this.society = society;
    this.description = description;
    this.type = type;
    this.status = status;
    this.uid = uid;
  }

  public Issue(String title, String description, String area) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getSociety() {
    return society;
  }

  public void setSociety(String society) {
    this.society = society;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

}
