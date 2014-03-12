package com.mobi.bright.minerva.dao;


import android.database.Cursor;

public final class Title extends Dao {
// ------------------------------ FIELDS ------------------------------

  String name;
  String language;
  String author;
  String publisher;
  int thothId;
  String created;
  String version;

// --------------------------- CONSTRUCTORS ---------------------------

  public Title() {
    super();
    getFromDb();
  }

  private void getFromDb() {
    Cursor cursor = getSqlLite().rawQuery("select Name, Language, Author, PublisherName, " +
                                      "ThothId, Created, Version " +
                                      "from title",
                                      null);

    if (cursor.moveToFirst()) {
      name = cursor.getString(0);
      language = cursor.getString(1);
      author = cursor.getString(2);
      publisher = cursor.getString(3);
      thothId = cursor.getInt(4);
      created = cursor.getString(5);
      version = cursor.getString(6);
    }

    cursor.close();
  }

// --------------------- GETTER / SETTER METHODS ---------------------

  public String getAuthor() {
    return author;
  }

  public String getCreated() {
    return created;
  }

  public String getLanguage() {
    return language;
  }

  public String getName() {
    return name;
  }

  public String getPublisher() {
    return publisher;
  }

  public int getThothId() {
    return thothId;
  }

  public String getVersion() {
    return version;
  }
}
