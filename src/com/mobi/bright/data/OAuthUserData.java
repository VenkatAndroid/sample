package com.mobi.bright.data;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OAuthUserData implements Serializable {
// ------------------------------ FIELDS ------------------------------

  //"08/06/1965"
  final private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
  String id;
  String name;
  String first_name;
  String last_name;
  String username;
  String birthday;
  String email;
  String gender;
  String errorReason;
  String type;
  int mobiId;


  boolean haveAvatar;

// --------------------------- CONSTRUCTORS ---------------------------

  public OAuthUserData(String errorReason) {
    this.errorReason = errorReason;
  }

// --------------------- GETTER / SETTER METHODS ---------------------

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getErrorReason() {
    return errorReason;
  }

  public void setErrorReason(String errorReason) {
    this.errorReason = errorReason;
  }

  public String getFirst_name() {
    return first_name;
  }

  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLast_name() {
    return last_name;
  }

  public void setLast_name(String last_name) {
    this.last_name = last_name;
  }

  public int getMobiId() {
    return mobiId;
  }

  public void setMobiId(int mobiId) {
    this.mobiId = mobiId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean isHaveAvatar() {
    return haveAvatar;
  }

  public void setHaveAvatar(boolean haveAvatar) {
    this.haveAvatar = haveAvatar;
  }

// ------------------------ CANONICAL METHODS ------------------------

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FacebookUserData{");
    sb.append("id=").append(id);
    sb.append(", name='").append(name).append('\'');
    sb.append(", first_name='").append(first_name).append('\'');
    sb.append(", last_name='").append(last_name).append('\'');
    sb.append(", username='").append(username).append('\'');
    sb.append(", birthday='").append(birthday).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append('}');
    return sb.toString();
  }

// -------------------------- OTHER METHODS --------------------------

  public Date computeBirthdayDate() {
    if (birthday != null) {
      try {
        return dateFormat.parse(birthday);
      }
      catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}
