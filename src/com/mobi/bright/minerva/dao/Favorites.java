package com.mobi.bright.minerva.dao;


import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class Favorites extends Dao {
    // ------------------------------ FIELDS ------------------------------
    public static final String TABLE_NAME = "favorites";
    int pageInstanceId;
    long unixTime;

// --------------------------- CONSTRUCTORS ---------------------------

    public Favorites() {
        super();
    }

    public List<Integer> getPageInstanceIds() {
        List<Integer> list = new ArrayList<Integer>();

        Cursor cursor = getSqlLite().rawQuery("Select  PageInstanceId from " + TABLE_NAME + " ORDER BY PageInstanceId ASC",
                                          null);

        while (cursor.moveToNext()) {
            list.add(cursor.getInt(0));
        }

        cursor.close();

        return list;
    }

// -------------------------- OTHER METHODS --------------------------

    public void add(int id) {
        if (getLast(id) == null) { // no history for page insert
            insert(id);
        } else {  // we have history update
            update(id);
        }
    }

    public Long getLast(int id) {
        Long last = null;

        Cursor cursor = getSqlLite().rawQuery("Select UnixTime from " + TABLE_NAME + " where PageInstanceId = ?",
                                          new String[]{
                                                  "" + id,
                                          });

        if (cursor.moveToNext()) {
            last = cursor.getLong(0);
        }

        cursor.close();

        //MobiLog.l(this,
        //          "last=" + last);

        return last;
    }

    private void insert(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PageInstanceId",
                          id);
        contentValues.put("UnixTime",
                          new Date().getTime());
      getSqlLite().insert(TABLE_NAME,
                        null,
                        contentValues);
    }

    private void update(int id) {
        String strFilter = "PageInstanceId=" + id;
        ContentValues contentValues = new ContentValues();
        contentValues.put("UnixTime",
                          new Date().getTime());
      getSqlLite().update(TABLE_NAME,
                        contentValues,
                        strFilter,
                        null);
    }

    public void clear() {
        getSqlLite().delete("favorites",
                        null,
                        null);
    }

    public List<Integer> getPageInstanceIds(Integer id) {
        List<Integer> list = new ArrayList<Integer>();

        Cursor cursor = getSqlLite().rawQuery("Select  PageInstanceId from " + TABLE_NAME + " where PageInstanceId=?",
                                          new String[]{
                                                  "" + id,
                                          });

        while (cursor.moveToNext()) {
            list.add(cursor.getInt(0));
        }

        cursor.close();

        return list;
    }

    public void remove(int id) {

        getSqlLite().delete(TABLE_NAME,
                        "PageInstanceId=?",
                        new String[]{"" + id});
    }
}
