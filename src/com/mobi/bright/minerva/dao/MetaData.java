package com.mobi.bright.minerva.dao;


import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.HashMap;
import java.util.Map;

public final class MetaData extends Dao {
// --------------------------- CONSTRUCTORS ---------------------------

    public MetaData() {
        super();
    }

// -------------------------- OTHER METHODS --------------------------

    public Map<String, String> getMetaData() {
        Map<String, String> map = new HashMap<String, String>();

        try {
            Cursor cursor = getSqlLite()
                    .rawQuery("select Name, Value " +
                              "from meta_data",
                              null);

            while (cursor.moveToNext()) {
                map.put(cursor.getString(1),
                        cursor.getString(1));
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return map;
    }
}
