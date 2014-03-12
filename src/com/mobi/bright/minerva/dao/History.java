package com.mobi.bright.minerva.dao;


import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

public final class History extends Dao {
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_NAME = "history";
    int pageInstanceId;
    long unixTime;

// --------------------------- CONSTRUCTORS ---------------------------

    public History() {
        super();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public int getPageInstanceId() {
        return pageInstanceId;
    }

    public long getUnixTime() {
        return unixTime;
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
        getSqlLite().delete(TABLE_NAME,
                        null,
                        null);
    }

    public boolean containsEntries() {
        int last = 0;

        Cursor cursor = getSqlLite().rawQuery("Select count(*) from " + TABLE_NAME,
                                          null);

        if (cursor.moveToNext()) {
            last = cursor.getInt(0);
        }

        //MobiLog.l(this,
        //          "last=" + last);

        return last > 0;
    }
}

