package com.mobi.bright.minerva.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobi.analytics.commom.json.CustomerJson;
import com.mobi.bright.minerva.sql_lite.ThothDatabase;
import com.mobi.bright.util.LogUtil;


public final class CustomerDao extends CustomerJson {


    public static final String TABLE_NAME = "customer";
    private static final String TAG = LogUtil.createTag(CustomerDao.class);

    Dao dao;

    public CustomerDao() {
      dao = new Dao();
    }


    public static final String SELECT =
            " Name, Type, MobiId, HaveAvatar ";


    String getUpdateString() {
        String update = SELECT.replace(",",
                                       "=?,");
        update = update.trim();
        return " " + update + "=? ";
    }

    public void updateDb() {

        String strSQL = "UPDATE " + TABLE_NAME +
                        " set " + getUpdateString();

        dao.getSqlLite().execSQL(strSQL,
                             new String[]{name,
                                          type,
                                          "" + mobiId,
                                          "" + haveAvatar});

    }


    public boolean getFromDb() {
        Cursor cursor = dao.getSqlLite()
                .rawQuery("select" + SELECT +
                          "from " + TABLE_NAME,
                          null);

        boolean haveNext = false;

        if (cursor.moveToNext()) {

            name = cursor.getString(0);
            type = cursor.getString(1);
            mobiId = cursor.getInt(2);

            int temp = cursor.getInt(3);

            if (temp > 0) {
                haveAvatar = true;
            } else {
                haveAvatar = false;
            }

            haveNext = true;

        }

        cursor.close();

        return haveNext;
    }

    public void insertDb() {

        ContentValues contentValues = new ContentValues();
        contentValues.put("Name",
                          name);
        contentValues.put("Type",
                          type);
        contentValues.put("MobiId",
                          mobiId);
        contentValues.put("HaveAvatar",
                          haveAvatar);

        SQLiteDatabase sqLiteDb = ThothDatabase.getInstance()
                                               .getDatabase();

        sqLiteDb.insert(TABLE_NAME,
                        null,
                        contentValues);
    }
}
