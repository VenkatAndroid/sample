package com.mobi.bright.minerva.dao;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public final class Page extends Dao {
// ------------------------------ FIELDS ------------------------------

  int orderNum;
  int id;
  String name;
  String uiType;
  String styleTemplate;
  String scriptTemplate;
  Double price;
  Integer isActivated;

// --------------------------- CONSTRUCTORS ---------------------------

  public Page() {
    super();
  }

// --------------------- GETTER / SETTER METHODS ---------------------

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(int orderNum) {
    this.orderNum = orderNum;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getScriptTemplate() {
    return scriptTemplate;
  }

  public void setScriptTemplate(String scriptTemplate) {
    this.scriptTemplate = scriptTemplate;
  }

  public String getStyleTemplate() {
    return styleTemplate;
  }

  public void setStyleTemplate(String styleTemplate) {
    this.styleTemplate = styleTemplate;
  }

  public String getUiType() {
    return uiType;
  }

  public void setUiType(String uiType) {
    this.uiType = uiType;
  }

// -------------------------- OTHER METHODS --------------------------

  public int findMinId() {
    int minId = -1;

    Cursor cursor = getSqlLite().rawQuery("Select min(PagesId) from pages",
                                      null);

    if (cursor.moveToNext()) {
      minId = cursor.getInt(0);
    }

    cursor.close();

    return minId;
  }

  public Integer getActivated() {
    return isActivated;
  }

  public List<Page> getPages() {
    List<Page> list = new ArrayList<Page>();

    Cursor cursor =
            getSqlLite().rawQuery(
                    "Select PagesId, Name, OrderNum,OrderNum,UiType,StyleTemplate,ScriptTemplate," +
                    "Price,isActivated " +
                    "from pages order by OrderNum ASC",
                    null);

    while (cursor.moveToNext()) {
      Page page = new Page();
      page.id = cursor.getInt(0);
      page.name = cursor.getString(1);
      page.orderNum = cursor.getInt(2);
      page.uiType = cursor.getString(3);
      page.styleTemplate = cursor.getString(4);
      page.scriptTemplate = cursor.getString(5);
      page.price = cursor.getDouble(6);
      page.isActivated = cursor.getInt(7);
      list.add(page);
    }


    return list;
  }

  public void setActivated(Integer activated) {
    isActivated = activated;
  }
}
